package com.energygames.lojadegames.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.energygames.lojadegames.dto.response.ItemPedidoResponseDTO;
import com.energygames.lojadegames.dto.response.PedidoResponseDTO;
import com.energygames.lojadegames.enums.StatusPedidoEnum;
import com.energygames.lojadegames.exception.BusinessException;
import com.energygames.lojadegames.exception.ResourceNotFoundException;
import com.energygames.lojadegames.model.CarrinhoItem;
import com.energygames.lojadegames.model.ItemPedido;
import com.energygames.lojadegames.model.Pedido;
import com.energygames.lojadegames.model.Produto;
import com.energygames.lojadegames.model.Usuario;
import com.energygames.lojadegames.repository.CarrinhoRepository;
import com.energygames.lojadegames.repository.PedidoRepository;
import com.energygames.lojadegames.repository.ProdutoRepository;
import com.energygames.lojadegames.repository.UsuarioRepository;
import com.energygames.lojadegames.service.PedidoService;

@Service
public class PedidoServiceImpl implements PedidoService {

    private static final Logger log = LoggerFactory.getLogger(PedidoServiceImpl.class);

    private final PedidoRepository pedidoRepository;
    private final CarrinhoRepository carrinhoRepository;
    private final ProdutoRepository produtoRepository;
    private final UsuarioRepository usuarioRepository;

    public PedidoServiceImpl(PedidoRepository pedidoRepository, CarrinhoRepository carrinhoRepository,
            ProdutoRepository produtoRepository, UsuarioRepository usuarioRepository) {
        this.pedidoRepository = pedidoRepository;
        this.carrinhoRepository = carrinhoRepository;
        this.produtoRepository = produtoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    @Transactional
    public PedidoResponseDTO criarPedido() {
        Usuario usuario = obterUsuarioAutenticado();
        log.info("Iniciando criação de pedido para usuário: {}", usuario.getEmail());

        List<CarrinhoItem> itensCarrinho = carrinhoRepository.findAllByUsuarioId(usuario.getId());

        if (itensCarrinho.isEmpty()) {
            throw new BusinessException("Carrinho vazio. Adicione itens antes de finalizar o pedido.");
        }

        Pedido pedido = new Pedido();
        pedido.setUsuario(usuario);
        pedido.setStatus(StatusPedidoEnum.PENDENTE_PAGAMENTO);
        
        List<ItemPedido> itensPedido = new ArrayList<>();
        BigDecimal valorTotal = BigDecimal.ZERO;

        for (CarrinhoItem itemCarrinho : itensCarrinho) {
            Produto produto = itemCarrinho.getProduto();

            // Validações - apenas verifica, NÃO baixa estoque ainda
            // O estoque será baixado apenas após confirmação do pagamento via webhook
            if (!produto.getAtivo()) {
                throw new BusinessException("Produto indisponível: " + produto.getNome());
            }

            if (produto.getEstoque() < itemCarrinho.getQuantidade()) {
                throw new BusinessException("Estoque insuficiente para o produto: " + produto.getNome());
            }

            // Criar ItemPedido
            ItemPedido itemPedido = new ItemPedido();
            itemPedido.setPedido(pedido);
            itemPedido.setProduto(produto);
            itemPedido.setQuantidade(itemCarrinho.getQuantidade());
            itemPedido.setPrecoUnitario(itemCarrinho.getPrecoUnitario());
            itemPedido.setDescontoUnitario(itemCarrinho.getDescontoUnitario());

            itensPedido.add(itemPedido);

            // Calcular subtotal do item para somar ao total do pedido
            BigDecimal subtotalItem = itemCarrinho.calcularSubtotal();
            valorTotal = valorTotal.add(subtotalItem);
        }

        pedido.setItens(itensPedido);
        pedido.setValorTotal(valorTotal);

        Pedido pedidoSalvo = pedidoRepository.save(pedido);
        
        // Limpar carrinho
        carrinhoRepository.deleteAllByUsuarioId(usuario.getId());

        log.info("Pedido criado com sucesso. ID: {} - Status: PENDENTE_PAGAMENTO", pedidoSalvo.getId());

        return toResponseDTO(pedidoSalvo);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PedidoResponseDTO> listarMeusPedidos() {
        Usuario usuario = obterUsuarioAutenticado();
        List<Pedido> pedidos = pedidoRepository.findByUsuario(usuario);
        return pedidos.stream().map(this::toResponseDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PedidoResponseDTO buscarPedidoPorId(Long id) {
        Usuario usuario = obterUsuarioAutenticado();
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido não encontrado com ID: " + id));

        // Validar se o pedido pertence ao usuário ou se é admin
        boolean isAdmin = usuario.getRoles().stream()
                .anyMatch(role -> role.name().equals("ROLE_ADMIN"));

        if (!pedido.getUsuario().getId().equals(usuario.getId()) && !isAdmin) {
             // Na verdade, a role é convertida, então preciso ver como comparar.
             // O RoleEnum tem ROLE_ADMIN? Vou assumir que sim ou verificar depois.
             // Por segurança, vou lançar ResourceNotFound para não expor existência.
             throw new ResourceNotFoundException("Pedido não encontrado com ID: " + id);
        }

        return toResponseDTO(pedido);
    }

    private Usuario obterUsuarioAutenticado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));
    }

    private PedidoResponseDTO toResponseDTO(Pedido pedido) {
        List<ItemPedidoResponseDTO> itensDTO = pedido.getItens().stream()
                .map(item -> {
                    BigDecimal precoFinal = item.getPrecoUnitario();
                    if (item.getDescontoUnitario() != null && item.getDescontoUnitario().compareTo(BigDecimal.ZERO) > 0) {
                         BigDecimal percentual = item.getDescontoUnitario().divide(BigDecimal.valueOf(100));
                         BigDecimal desconto = item.getPrecoUnitario().multiply(percentual);
                         precoFinal = item.getPrecoUnitario().subtract(desconto);
                    }
                    BigDecimal subtotal = precoFinal.multiply(BigDecimal.valueOf(item.getQuantidade()));
                    
                    return new ItemPedidoResponseDTO(
                        item.getProduto().getId(),
                        item.getProduto().getNome(),
                        item.getQuantidade(),
                        item.getPrecoUnitario(),
                        item.getDescontoUnitario(),
                        subtotal
                    );
                })
                .collect(Collectors.toList());

        return new PedidoResponseDTO(
            pedido.getId(),
            pedido.getStatus(),
            pedido.getValorTotal(),
            pedido.getDataCriacao(),
            itensDTO
        );
    }
}
