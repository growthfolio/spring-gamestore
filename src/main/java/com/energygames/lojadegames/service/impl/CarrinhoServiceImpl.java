package com.energygames.lojadegames.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.energygames.lojadegames.dto.mapper.CarrinhoMapper;
import com.energygames.lojadegames.dto.request.CarrinhoRequestDTO;
import com.energygames.lojadegames.dto.response.CarrinhoItemResponseDTO;
import com.energygames.lojadegames.dto.response.CarrinhoResumoResponseDTO;
import com.energygames.lojadegames.exception.BusinessException;
import com.energygames.lojadegames.exception.ResourceNotFoundException;
import com.energygames.lojadegames.model.CarrinhoItem;
import com.energygames.lojadegames.model.Produto;
import com.energygames.lojadegames.model.Usuario;
import com.energygames.lojadegames.repository.CarrinhoRepository;
import com.energygames.lojadegames.repository.ProdutoRepository;
import com.energygames.lojadegames.repository.UsuarioRepository;
import com.energygames.lojadegames.service.CarrinhoService;

@Service
public class CarrinhoServiceImpl implements CarrinhoService {

	private static final Logger log = LoggerFactory.getLogger(CarrinhoServiceImpl.class);

	private final CarrinhoRepository carrinhoRepository;
	private final ProdutoRepository produtoRepository;
	private final UsuarioRepository usuarioRepository;
	private final CarrinhoMapper carrinhoMapper;

	public CarrinhoServiceImpl(CarrinhoRepository carrinhoRepository, ProdutoRepository produtoRepository,
			UsuarioRepository usuarioRepository, CarrinhoMapper carrinhoMapper) {
		this.carrinhoRepository = carrinhoRepository;
		this.produtoRepository = produtoRepository;
		this.usuarioRepository = usuarioRepository;
		this.carrinhoMapper = carrinhoMapper;
	}

	@Override
	@Transactional
	public CarrinhoItemResponseDTO adicionar(CarrinhoRequestDTO dto) {
		log.info("Adicionando produto {} ao carrinho. Quantidade: {}", dto.getProdutoId(), dto.getQuantidade());

		Usuario usuario = obterUsuarioAutenticado();

		// Validar produto
		Produto produto = produtoRepository.findById(dto.getProdutoId())
				.orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado com ID: " + dto.getProdutoId()));

		// Validações de negócio
		validarProdutoParaCarrinho(produto, dto.getQuantidade());

		// Verificar se já existe no carrinho
		return carrinhoRepository.findByUsuarioIdAndProdutoId(usuario.getId(), dto.getProdutoId())
				.map(itemExistente -> {
					log.info("Produto já no carrinho. Atualizando quantidade de {} para {}",
							itemExistente.getQuantidade(), itemExistente.getQuantidade() + dto.getQuantidade());
					
					int novaQuantidade = itemExistente.getQuantidade() + dto.getQuantidade();
					validarEstoque(produto, novaQuantidade);
					
					itemExistente.setQuantidade(novaQuantidade);
					CarrinhoItem atualizado = carrinhoRepository.save(itemExistente);
					return carrinhoMapper.toItemResponseDTO(atualizado);
				})
				.orElseGet(() -> {
					log.info("Criando novo item no carrinho");
					CarrinhoItem novoItem = new CarrinhoItem();
					novoItem.setUsuario(usuario);
					novoItem.setProduto(produto);
					novoItem.setQuantidade(dto.getQuantidade());
					novoItem.setPrecoUnitario(produto.getPreco());
					novoItem.setDescontoUnitario(produto.getDesconto());
					
					CarrinhoItem salvo = carrinhoRepository.save(novoItem);
					log.info("Item adicionado ao carrinho. ID: {}", salvo.getId());
					return carrinhoMapper.toItemResponseDTO(salvo);
				});
	}

	@Override
	@Transactional
	public CarrinhoItemResponseDTO atualizarQuantidade(Long produtoId, Integer quantidade) {
		log.info("Atualizando quantidade do produto {} no carrinho para: {}", produtoId, quantidade);

		if (quantidade < 1) {
			throw new BusinessException("Quantidade deve ser no mínimo 1");
		}

		Usuario usuario = obterUsuarioAutenticado();

		CarrinhoItem item = carrinhoRepository.findByUsuarioIdAndProdutoId(usuario.getId(), produtoId)
				.orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado no carrinho"));

		validarEstoque(item.getProduto(), quantidade);

		item.setQuantidade(quantidade);
		CarrinhoItem atualizado = carrinhoRepository.save(item);

		log.info("Quantidade atualizada com sucesso");
		return carrinhoMapper.toItemResponseDTO(atualizado);
	}

	@Override
	@Transactional
	public void remover(Long produtoId) {
		log.info("Removendo produto {} do carrinho", produtoId);

		Usuario usuario = obterUsuarioAutenticado();

		CarrinhoItem item = carrinhoRepository.findByUsuarioIdAndProdutoId(usuario.getId(), produtoId)
				.orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado no carrinho"));

		carrinhoRepository.delete(item);
		log.info("Produto removido do carrinho com sucesso");
	}

	@Override
	@Transactional
	public void limpar() {
		log.info("Limpando carrinho do usuário autenticado");

		Usuario usuario = obterUsuarioAutenticado();
		carrinhoRepository.deleteAllByUsuarioId(usuario.getId());

		log.info("Carrinho limpo com sucesso");
	}

	@Override
	@Transactional(readOnly = true)
	public CarrinhoResumoResponseDTO obterResumo() {
		log.info("Obtendo resumo do carrinho");

		Usuario usuario = obterUsuarioAutenticado();
		List<CarrinhoItem> itens = carrinhoRepository.findAllByUsuarioId(usuario.getId());

		CarrinhoResumoResponseDTO resumo = new CarrinhoResumoResponseDTO();
		
		List<CarrinhoItemResponseDTO> itensDTO = itens.stream()
				.map(carrinhoMapper::toItemResponseDTO)
				.collect(Collectors.toList());
		
		resumo.setItens(itensDTO);
		resumo.setTotalItens(itens.size());
		resumo.setTotalProdutos(itens.stream().mapToInt(CarrinhoItem::getQuantidade).sum());
		
		BigDecimal subtotal = itens.stream()
				.map(item -> item.getPrecoUnitario().multiply(BigDecimal.valueOf(item.getQuantidade())))
				.reduce(BigDecimal.ZERO, BigDecimal::add);
		
		BigDecimal total = itens.stream()
				.map(CarrinhoItem::calcularSubtotal)
				.reduce(BigDecimal.ZERO, BigDecimal::add);
		
		BigDecimal descontoTotal = subtotal.subtract(total);
		
		resumo.setSubtotal(subtotal.setScale(2, java.math.RoundingMode.HALF_UP));
		resumo.setDescontoTotal(descontoTotal.setScale(2, java.math.RoundingMode.HALF_UP));
		resumo.setTotal(total.setScale(2, java.math.RoundingMode.HALF_UP));

		log.info("Resumo gerado: {} itens, Total: R$ {}", resumo.getTotalItens(), resumo.getTotal());
		return resumo;
	}

	@Override
	@Transactional(readOnly = true)
	public Integer contarItens() {
		Usuario usuario = obterUsuarioAutenticado();
		Integer total = carrinhoRepository.somarQuantidadePorUsuario(usuario.getId());
		return total != null ? total : 0;
	}

	private void validarProdutoParaCarrinho(Produto produto, Integer quantidade) {
		if (produto.getAtivo() == null || !produto.getAtivo()) {
			log.warn("Tentativa de adicionar produto inativo ao carrinho. Produto ID: {}", produto.getId());
			throw new BusinessException("Produto inativo não pode ser adicionado ao carrinho");
		}

		validarEstoque(produto, quantidade);
	}

	private void validarEstoque(Produto produto, Integer quantidade) {
		if (produto.getEstoque() == null || produto.getEstoque() < quantidade) {
			log.warn("Estoque insuficiente. Produto ID: {}, Solicitado: {}, Disponível: {}",
					produto.getId(), quantidade, produto.getEstoque());
			throw new BusinessException(String.format(
					"Estoque insuficiente. Disponível: %d unidade(s)", 
					produto.getEstoque() != null ? produto.getEstoque() : 0));
		}
	}

	private Usuario obterUsuarioAutenticado() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || !authentication.isAuthenticated()) {
			throw new BusinessException("Usuário não autenticado");
		}

		String email = authentication.getName();
		return usuarioRepository.findByEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado: " + email));
	}
}
