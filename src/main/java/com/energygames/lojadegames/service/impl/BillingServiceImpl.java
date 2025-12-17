package com.energygames.lojadegames.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.energygames.lojadegames.configuration.StripeConfig;
import com.energygames.lojadegames.dto.request.CheckoutRequestDTO;
import com.energygames.lojadegames.dto.response.CheckoutResponseDTO;
import com.energygames.lojadegames.dto.response.PaymentStatusDTO;
import com.energygames.lojadegames.enums.StatusPedidoEnum;
import com.energygames.lojadegames.exception.BusinessException;
import com.energygames.lojadegames.exception.ResourceNotFoundException;
import com.energygames.lojadegames.model.ItemPedido;
import com.energygames.lojadegames.model.Pedido;
import com.energygames.lojadegames.model.PaymentEvent;
import com.energygames.lojadegames.model.Produto;
import com.energygames.lojadegames.model.Usuario;
import com.energygames.lojadegames.repository.PaymentEventRepository;
import com.energygames.lojadegames.repository.PedidoRepository;
import com.energygames.lojadegames.repository.ProdutoRepository;
import com.energygames.lojadegames.repository.UsuarioRepository;
import com.energygames.lojadegames.service.BillingService;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;

@Service
public class BillingServiceImpl implements BillingService {

    private static final Logger log = LoggerFactory.getLogger(BillingServiceImpl.class);

    private final PedidoRepository pedidoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProdutoRepository produtoRepository;
    private final PaymentEventRepository paymentEventRepository;
    private final StripeConfig stripeConfig;

    public BillingServiceImpl(
            PedidoRepository pedidoRepository,
            UsuarioRepository usuarioRepository,
            ProdutoRepository produtoRepository,
            PaymentEventRepository paymentEventRepository,
            StripeConfig stripeConfig) {
        this.pedidoRepository = pedidoRepository;
        this.usuarioRepository = usuarioRepository;
        this.produtoRepository = produtoRepository;
        this.paymentEventRepository = paymentEventRepository;
        this.stripeConfig = stripeConfig;
    }

    @Override
    @Transactional
    public CheckoutResponseDTO createCheckoutSession(CheckoutRequestDTO request) {
        Usuario usuario = obterUsuarioAutenticado();
        
        Pedido pedido = pedidoRepository.findById(request.pedidoId())
                .orElseThrow(() -> new ResourceNotFoundException("Pedido não encontrado: " + request.pedidoId()));

        // Validar se o pedido pertence ao usuário
        if (!pedido.getUsuario().getId().equals(usuario.getId())) {
            throw new ResourceNotFoundException("Pedido não encontrado: " + request.pedidoId());
        }

        // Validar status do pedido
        if (pedido.getStatus() != StatusPedidoEnum.PENDENTE_PAGAMENTO) {
            throw new BusinessException("Pedido não está disponível para pagamento. Status atual: " + pedido.getStatus());
        }

        // Verificar se já existe uma session para este pedido
        if (pedido.getStripeSessionId() != null) {
            try {
                Session existingSession = Session.retrieve(pedido.getStripeSessionId());
                if ("open".equals(existingSession.getStatus())) {
                    return new CheckoutResponseDTO(existingSession.getUrl(), existingSession.getId());
                }
            } catch (StripeException e) {
                log.warn("Sessão anterior expirou ou inválida, criando nova: {}", e.getMessage());
            }
        }

        try {
            // Criar line items para o Stripe
            List<SessionCreateParams.LineItem> lineItems = new ArrayList<>();
            
            for (ItemPedido item : pedido.getItens()) {
                BigDecimal precoFinal = item.getPrecoUnitario();
                if (item.getDescontoUnitario() != null && item.getDescontoUnitario().compareTo(BigDecimal.ZERO) > 0) {
                    BigDecimal percentual = item.getDescontoUnitario().divide(BigDecimal.valueOf(100));
                    BigDecimal desconto = item.getPrecoUnitario().multiply(percentual);
                    precoFinal = item.getPrecoUnitario().subtract(desconto);
                }
                
                // Stripe trabalha com centavos
                long priceInCents = precoFinal.multiply(BigDecimal.valueOf(100)).longValue();
                
                SessionCreateParams.LineItem lineItem = SessionCreateParams.LineItem.builder()
                        .setPriceData(
                            SessionCreateParams.LineItem.PriceData.builder()
                                .setCurrency("brl")
                                .setUnitAmount(priceInCents)
                                .setProductData(
                                    SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                        .setName(item.getProduto().getNome())
                                        .setDescription("Jogo digital - " + item.getProduto().getNome())
                                        .build()
                                )
                                .build()
                        )
                        .setQuantity((long) item.getQuantidade())
                        .build();
                
                lineItems.add(lineItem);
            }

            // Criar Checkout Session
            SessionCreateParams params = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setSuccessUrl(stripeConfig.getSuccessUrl() + "?session_id={CHECKOUT_SESSION_ID}")
                    .setCancelUrl(stripeConfig.getCancelUrl() + "?pedido_id=" + pedido.getId())
                    .setCustomerEmail(usuario.getEmail())
                    .putMetadata("pedido_id", pedido.getId().toString())
                    .putMetadata("user_id", usuario.getId().toString())
                    .addAllLineItem(lineItems)
                    .build();

            Session session = Session.create(params);
            
            // Salvar session ID no pedido
            pedido.setStripeSessionId(session.getId());
            pedidoRepository.save(pedido);

            log.info("Checkout Session criada para pedido {}: {}", pedido.getId(), session.getId());

            return new CheckoutResponseDTO(session.getUrl(), session.getId());

        } catch (StripeException e) {
            log.error("Erro ao criar Checkout Session para pedido {}: {}", pedido.getId(), e.getMessage());
            throw new BusinessException("Erro ao processar pagamento. Tente novamente.");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentStatusDTO getPaymentStatus(String sessionId) {
        Pedido pedido = pedidoRepository.findByStripeSessionId(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Sessão de pagamento não encontrada"));

        String status = switch (pedido.getStatus()) {
            case PAGO -> "paid";
            case CANCELADO -> "cancelled";
            case PENDENTE_PAGAMENTO -> "pending";
            default -> "pending";
        };

        return new PaymentStatusDTO(pedido.getId(), status, pedido.getValorTotal());
    }

    @Override
    @Transactional
    public void handlePaymentSuccess(String sessionId, String stripeEventId) {
        // Verificar idempotência
        if (paymentEventRepository.existsByStripeEventId(stripeEventId)) {
            log.info("Evento já processado (idempotência): {}", stripeEventId);
            return;
        }

        Pedido pedido = pedidoRepository.findByStripeSessionId(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido não encontrado para session: " + sessionId));

        // Verificar se já foi pago
        if (pedido.getStatus() == StatusPedidoEnum.PAGO) {
            log.info("Pedido {} já está pago", pedido.getId());
            return;
        }

        // Baixar estoque
        for (ItemPedido item : pedido.getItens()) {
            Produto produto = item.getProduto();
            
            if (produto.getEstoque() < item.getQuantidade()) {
                log.error("Estoque insuficiente para produto {} ao processar pagamento", produto.getId());
                // Marcar como problema - necessário tratamento manual
                pedido.setStatus(StatusPedidoEnum.CANCELADO);
                pedidoRepository.save(pedido);
                throw new BusinessException("Estoque insuficiente para o produto: " + produto.getNome());
            }
            
            produto.setEstoque(produto.getEstoque() - item.getQuantidade());
            produtoRepository.save(produto);
        }

        // Atualizar status
        pedido.setStatus(StatusPedidoEnum.PAGO);
        pedidoRepository.save(pedido);

        // Registrar evento para idempotência
        PaymentEvent event = new PaymentEvent();
        event.setStripeEventId(stripeEventId);
        event.setStripeSessionId(sessionId);
        event.setPedidoId(pedido.getId());
        event.setEventType("checkout.session.completed");
        paymentEventRepository.save(event);

        log.info("Pagamento processado com sucesso para pedido {}", pedido.getId());
    }

    @Override
    @Transactional
    public void handlePaymentFailure(String sessionId, String stripeEventId) {
        // Verificar idempotência
        if (paymentEventRepository.existsByStripeEventId(stripeEventId)) {
            log.info("Evento já processado (idempotência): {}", stripeEventId);
            return;
        }

        Pedido pedido = pedidoRepository.findByStripeSessionId(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido não encontrado para session: " + sessionId));

        pedido.setStatus(StatusPedidoEnum.CANCELADO);
        pedidoRepository.save(pedido);

        // Registrar evento
        PaymentEvent event = new PaymentEvent();
        event.setStripeEventId(stripeEventId);
        event.setStripeSessionId(sessionId);
        event.setPedidoId(pedido.getId());
        event.setEventType("checkout.session.expired");
        paymentEventRepository.save(event);

        log.info("Pagamento falhou/expirou para pedido {}", pedido.getId());
    }

    private Usuario obterUsuarioAutenticado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));
    }
}
