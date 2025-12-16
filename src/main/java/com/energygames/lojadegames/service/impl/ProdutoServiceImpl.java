package com.energygames.lojadegames.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.energygames.lojadegames.dto.mapper.ProdutoMapper;
import com.energygames.lojadegames.dto.request.ProdutoComercialUpdateDTO;
import com.energygames.lojadegames.dto.request.ProdutoRequestDTO;
import com.energygames.lojadegames.dto.response.ProdutoDetalheResponseDTO;
import com.energygames.lojadegames.dto.response.ProdutoResponseDTO;
import com.energygames.lojadegames.enums.OrigemEnum;
import com.energygames.lojadegames.exception.BusinessException;
import com.energygames.lojadegames.exception.ResourceNotFoundException;
import com.energygames.lojadegames.model.Categoria;
import com.energygames.lojadegames.model.Produto;
import com.energygames.lojadegames.model.Usuario;
import com.energygames.lojadegames.repository.CategoriaRepository;
import com.energygames.lojadegames.repository.ProdutoRepository;
import com.energygames.lojadegames.repository.UsuarioRepository;
import com.energygames.lojadegames.service.ProdutoService;

@Service
public class ProdutoServiceImpl implements ProdutoService {

	private static final Logger log = LoggerFactory.getLogger(ProdutoServiceImpl.class);

	private final ProdutoRepository produtoRepository;
	private final CategoriaRepository categoriaRepository;
	private final UsuarioRepository usuarioRepository;
	private final ProdutoMapper produtoMapper;

	public ProdutoServiceImpl(ProdutoRepository produtoRepository, CategoriaRepository categoriaRepository,
			UsuarioRepository usuarioRepository, ProdutoMapper produtoMapper) {
		this.produtoRepository = produtoRepository;
		this.categoriaRepository = categoriaRepository;
		this.usuarioRepository = usuarioRepository;
		this.produtoMapper = produtoMapper;
	}

	@Override
	@Transactional(readOnly = true)
	public org.springframework.data.domain.Page<ProdutoResponseDTO> buscarTodos(
			String nome, Long categoriaId, org.springframework.data.domain.Pageable pageable) {
		log.info("Buscando produtos com filtros - nome: {}, categoriaId: {}", nome, categoriaId);
		
		org.springframework.data.domain.Page<com.energygames.lojadegames.model.Produto> produtos;
		
		if (nome != null && categoriaId != null) {
			produtos = produtoRepository.findAll(
				(root, query, cb) -> cb.and(
					cb.like(cb.lower(root.get("nome")), "%" + nome.toLowerCase() + "%"),
					cb.equal(root.get("categoria").get("id"), categoriaId)
				), pageable);
		} else if (nome != null) {
			produtos = produtoRepository.findAll(
				(root, query, cb) -> cb.like(cb.lower(root.get("nome")), "%" + nome.toLowerCase() + "%"), 
				pageable);
		} else if (categoriaId != null) {
			produtos = produtoRepository.findAll(
				(root, query, cb) -> cb.equal(root.get("categoria").get("id"), categoriaId), 
				pageable);
		} else {
			produtos = produtoRepository.findAll(pageable);
		}
		
		return produtos.map(produtoMapper::toResponseDTO);
	}

	@Override
	@Transactional(readOnly = true)
	public ProdutoResponseDTO buscarPorId(Long id) {
		log.info("Buscando produto com ID: {}", id);
		Produto produto = produtoRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado com ID: " + id));
		return produtoMapper.toResponseDTO(produto);
	}

	@Override
	@Transactional(readOnly = true)
	public ProdutoDetalheResponseDTO buscarDetalhePorId(Long id) {
		log.info("Buscando detalhe do produto com ID: {}", id);
		Produto produto = produtoRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado com ID: " + id));
		return produtoMapper.toDetalheResponseDTO(produto);
	}

	@Override
	@Transactional(readOnly = true)
	public ProdutoDetalheResponseDTO buscarDetalhePorSlug(String slug) {
		log.info("Buscando detalhe do produto com slug: {}", slug);
		Produto produto = produtoRepository.findBySlug(slug)
				.orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado com slug: " + slug));
		return produtoMapper.toDetalheResponseDTO(produto);
	}



	@Override
	@Transactional
	public ProdutoResponseDTO criar(ProdutoRequestDTO dto) {
		log.info("Criando novo produto: {}", dto.getNome());

		// Validar categoria
		Categoria categoria = categoriaRepository.findById(dto.getCategoriaId())
				.orElseThrow(() -> new ResourceNotFoundException(
						"Categoria não encontrada com ID: " + dto.getCategoriaId()));

		if (categoria.getAtivo() == null || !categoria.getAtivo()) {
			log.warn("Tentativa de criar produto em categoria inativa. Categoria ID: {}", categoria.getId());
			throw new BusinessException("Categoria inativa não pode receber produtos");
		}

		// Validar estoque
		if (dto.getEstoque() < 0) {
			log.warn("Tentativa de criar produto com estoque negativo: {}", dto.getEstoque());
			throw new BusinessException("Estoque não pode ser negativo");
		}

		// Validar desconto
		if (dto.getDesconto() != null && 
			(dto.getDesconto().compareTo(java.math.BigDecimal.ZERO) < 0 || 
			 dto.getDesconto().compareTo(java.math.BigDecimal.valueOf(100)) > 0)) {
			log.warn("Tentativa de criar produto com desconto inválido: {}", dto.getDesconto());
			throw new BusinessException("Desconto deve estar entre 0 e 100");
		}

		// Validar data de lançamento
		if (dto.getDataLancamento().isAfter(java.time.LocalDate.now())) {
			log.warn("Tentativa de criar produto com data de lançamento futura: {}", dto.getDataLancamento());
			throw new BusinessException("Data de lançamento não pode ser futura");
		}

		// Calcular preço com desconto
		java.math.BigDecimal precoFinal = calcularPrecoComDesconto(dto.getPreco(), dto.getDesconto());
		if (precoFinal.compareTo(java.math.BigDecimal.ZERO) <= 0) {
			log.warn("Preço final calculado é zero ou negativo. Preço: {}, Desconto: {}", dto.getPreco(), dto.getDesconto());
			throw new BusinessException("Preço final do produto não pode ser zero ou negativo");
		}

		// Obter usuário autenticado
		Usuario usuario = obterUsuarioAutenticado();

		Produto produto = produtoMapper.toEntity(dto, categoria, usuario);
		Produto produtoSalvo = produtoRepository.save(produto);

		log.info("Produto criado com sucesso. ID: {}, Preço: {}, Desconto: {}%", 
			produtoSalvo.getId(), produtoSalvo.getPreco(), produtoSalvo.getDesconto());
		return produtoMapper.toResponseDTO(produtoSalvo);
	}

	@Override
	@Transactional
	public ProdutoResponseDTO atualizar(Long id, ProdutoRequestDTO dto) {
		log.info("Atualizando produto ID: {}", id);

		Produto produto = produtoRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado com ID: " + id));

		// Validar categoria
		Categoria categoria = categoriaRepository.findById(dto.getCategoriaId())
				.orElseThrow(() -> new ResourceNotFoundException(
						"Categoria não encontrada com ID: " + dto.getCategoriaId()));

		if (categoria.getAtivo() == null || !categoria.getAtivo()) {
			log.warn("Tentativa de atualizar produto para categoria inativa. Produto ID: {}, Categoria ID: {}", 
				id, categoria.getId());
			throw new BusinessException("Categoria inativa não pode receber produtos");
		}

		// Validar estoque
		if (dto.getEstoque() < 0) {
			log.warn("Tentativa de atualizar produto com estoque negativo. Produto ID: {}", id);
			throw new BusinessException("Estoque não pode ser negativo");
		}

		// Validar desconto
		if (dto.getDesconto() != null && 
			(dto.getDesconto().compareTo(java.math.BigDecimal.ZERO) < 0 || 
			 dto.getDesconto().compareTo(java.math.BigDecimal.valueOf(100)) > 0)) {
			log.warn("Tentativa de atualizar produto com desconto inválido. Produto ID: {}, Desconto: {}", 
				id, dto.getDesconto());
			throw new BusinessException("Desconto deve estar entre 0 e 100");
		}

		// Validar data de lançamento
		if (dto.getDataLancamento().isAfter(java.time.LocalDate.now())) {
			log.warn("Tentativa de atualizar produto com data de lançamento futura. Produto ID: {}", id);
			throw new BusinessException("Data de lançamento não pode ser futura");
		}

		// Calcular preço com desconto
		java.math.BigDecimal precoFinal = calcularPrecoComDesconto(dto.getPreco(), dto.getDesconto());
		if (precoFinal.compareTo(java.math.BigDecimal.ZERO) <= 0) {
			log.warn("Preço final calculado é zero ou negativo na atualização. Produto ID: {}", id);
			throw new BusinessException("Preço final do produto não pode ser zero ou negativo");
		}

		produtoMapper.updateEntity(dto, produto, categoria);
		Produto produtoAtualizado = produtoRepository.save(produto);

		log.info("Produto atualizado com sucesso. ID: {}, Novo preço: {}, Novo desconto: {}%", 
			id, produtoAtualizado.getPreco(), produtoAtualizado.getDesconto());
		return produtoMapper.toResponseDTO(produtoAtualizado);
	}

	@Override
	@Transactional
	public void deletar(Long id) {
		log.info("Deletando produto ID: {}", id);

		// Verifica se produto existe antes de deletar
		if (!produtoRepository.existsById(id)) {
			throw new ResourceNotFoundException("Produto não encontrado com ID: " + id);
		}

		produtoRepository.deleteById(id);
		log.info("Produto deletado com sucesso. ID: {}", id);
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

	private java.math.BigDecimal calcularPrecoComDesconto(java.math.BigDecimal preco, java.math.BigDecimal desconto) {
		if (desconto == null || desconto.compareTo(java.math.BigDecimal.ZERO) == 0) {
			return preco;
		}
		java.math.BigDecimal percentualDesconto = desconto.divide(
			java.math.BigDecimal.valueOf(100), 4, java.math.RoundingMode.HALF_UP);
		java.math.BigDecimal valorDesconto = preco.multiply(percentualDesconto);
		return preco.subtract(valorDesconto).setScale(2, java.math.RoundingMode.HALF_UP);
	}

	@Override
	@Transactional
	public ProdutoResponseDTO atualizarDadosComerciais(Long id, ProdutoComercialUpdateDTO dto) {
		log.info("Atualizando dados comerciais do produto ID: {}", id);

		Produto produto = produtoRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado com ID: " + id));

		// Atualizar preço
		if (dto.getPreco() != null) {
			produto.setPreco(dto.getPreco());
		}

		// Atualizar estoque
		if (dto.getEstoque() != null) {
			produto.setEstoque(dto.getEstoque());
		}

		// Atualizar desconto
		if (dto.getDesconto() != null) {
			produto.setDesconto(dto.getDesconto());
		}

		// Atualizar status ativo (permitir ativar produto após definir preço/estoque)
		if (dto.getAtivo() != null) {
			produto.setAtivo(dto.getAtivo());
		}

		Produto produtoAtualizado = produtoRepository.save(produto);

		log.info("Dados comerciais atualizados. Produto ID: {}, Preço: {}, Estoque: {}, Ativo: {}", 
			id, produtoAtualizado.getPreco(), produtoAtualizado.getEstoque(), produtoAtualizado.getAtivo());

		return produtoMapper.toResponseDTO(produtoAtualizado);
	}

	@Override
	@Transactional(readOnly = true)
	public org.springframework.data.domain.Page<ProdutoResponseDTO> buscarProdutosPendentes(org.springframework.data.domain.Pageable pageable) {
		log.info("Buscando produtos pendentes de revisão (IGDB + Inativos)");
		return produtoRepository.findByAtivoFalseAndOrigemExternaOrigem(OrigemEnum.IGDB, pageable)
				.map(produtoMapper::toResponseDTO);
	}
}
