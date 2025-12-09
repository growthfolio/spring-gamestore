package com.energygames.lojadegames.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.energygames.lojadegames.dto.mapper.ProdutoMapper;
import com.energygames.lojadegames.dto.request.ProdutoRequestDTO;
import com.energygames.lojadegames.dto.response.ProdutoResponseDTO;
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
	public List<ProdutoResponseDTO> buscarTodos() {
		log.info("Buscando todos os produtos");
		return produtoRepository.findAll().stream()
				.map(produtoMapper::toResponseDTO)
				.collect(Collectors.toList());
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
	public List<ProdutoResponseDTO> buscarPorNome(String nome) {
		log.info("Buscando produtos por nome: {}", nome);
		return produtoRepository.findAllByNomeContainingIgnoreCase(nome).stream()
				.map(produtoMapper::toResponseDTO)
				.collect(Collectors.toList());
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
			throw new BusinessException("Categoria inativa não pode receber produtos");
		}

		// Validar estoque
		if (dto.getEstoque() < 0) {
			throw new BusinessException("Estoque não pode ser negativo");
		}

		// Validar desconto
		if (dto.getDesconto() != null && (dto.getDesconto() < 0 || dto.getDesconto() > 100)) {
			throw new BusinessException("Desconto deve estar entre 0 e 100");
		}

		// Obter usuário autenticado
		Usuario usuario = obterUsuarioAutenticado();

		Produto produto = produtoMapper.toEntity(dto, categoria, usuario);
		Produto produtoSalvo = produtoRepository.save(produto);

		log.info("Produto criado com sucesso. ID: {}", produtoSalvo.getId());
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
			throw new BusinessException("Categoria inativa não pode receber produtos");
		}

		// Validar estoque
		if (dto.getEstoque() < 0) {
			throw new BusinessException("Estoque não pode ser negativo");
		}

		// Validar desconto
		if (dto.getDesconto() != null && (dto.getDesconto() < 0 || dto.getDesconto() > 100)) {
			throw new BusinessException("Desconto deve estar entre 0 e 100");
		}

		produtoMapper.updateEntity(dto, produto, categoria);
		Produto produtoAtualizado = produtoRepository.save(produto);

		log.info("Produto atualizado com sucesso. ID: {}", id);
		return produtoMapper.toResponseDTO(produtoAtualizado);
	}

	@Override
	@Transactional
	public void deletar(Long id) {
		log.info("Deletando produto ID: {}", id);

		Produto produto = produtoRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado com ID: " + id));

		produtoRepository.deleteById(id);
		log.info("Produto deletado com sucesso. ID: {}", id);
	}

	private Usuario obterUsuarioAutenticado() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || !authentication.isAuthenticated()) {
			throw new BusinessException("Usuário não autenticado");
		}

		String email = authentication.getName();
		return usuarioRepository.findByUsuario(email)
				.orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado: " + email));
	}
}
