package com.energygames.lojadegames.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.energygames.lojadegames.dto.mapper.AvaliacaoMapper;
import com.energygames.lojadegames.dto.request.AvaliacaoRequestDTO;
import com.energygames.lojadegames.dto.response.AvaliacaoResponseDTO;
import com.energygames.lojadegames.exception.BusinessException;
import com.energygames.lojadegames.exception.ResourceNotFoundException;
import com.energygames.lojadegames.model.Avaliacao;
import com.energygames.lojadegames.model.Produto;
import com.energygames.lojadegames.model.Usuario;
import com.energygames.lojadegames.repository.AvaliacaoRepository;
import com.energygames.lojadegames.repository.ProdutoRepository;
import com.energygames.lojadegames.repository.UsuarioRepository;
import com.energygames.lojadegames.service.AvaliacaoService;

@Service
public class AvaliacaoServiceImpl implements AvaliacaoService {

	private static final Logger log = LoggerFactory.getLogger(AvaliacaoServiceImpl.class);

	private final AvaliacaoRepository avaliacaoRepository;
	private final ProdutoRepository produtoRepository;
	private final UsuarioRepository usuarioRepository;
	private final AvaliacaoMapper avaliacaoMapper;

	public AvaliacaoServiceImpl(AvaliacaoRepository avaliacaoRepository, ProdutoRepository produtoRepository,
			UsuarioRepository usuarioRepository, AvaliacaoMapper avaliacaoMapper) {
		this.avaliacaoRepository = avaliacaoRepository;
		this.produtoRepository = produtoRepository;
		this.usuarioRepository = usuarioRepository;
		this.avaliacaoMapper = avaliacaoMapper;
	}

	@Override
	@Transactional
	public AvaliacaoResponseDTO criar(AvaliacaoRequestDTO dto) {
		log.info("Criando nova avaliação para produto ID: {}", dto.getProdutoId());

		// Obter usuário autenticado
		Usuario usuario = obterUsuarioAutenticado();

		// Validar se produto existe
		Produto produto = produtoRepository.findById(dto.getProdutoId())
				.orElseThrow(() -> new ResourceNotFoundException(
						"Produto não encontrado com ID: " + dto.getProdutoId()));

		// Validar se usuário já avaliou este produto
		avaliacaoRepository.findByProdutoIdAndUsuarioId(produto.getId(), usuario.getId())
				.ifPresent(a -> {
					log.warn("Usuário {} já avaliou o produto {}", usuario.getId(), produto.getId());
					throw new BusinessException("Você já avaliou este produto. Use a atualização para modificar.");
				});

		Avaliacao avaliacao = avaliacaoMapper.toEntity(dto, produto, usuario);
		Avaliacao avaliacaoSalva = avaliacaoRepository.save(avaliacao);

		log.info("Avaliação criada com sucesso. ID: {}, Nota: {}", avaliacaoSalva.getId(), avaliacaoSalva.getNota());
		return avaliacaoMapper.toResponseDTO(avaliacaoSalva);
	}

	@Override
	@Transactional
	public AvaliacaoResponseDTO atualizar(Long id, AvaliacaoRequestDTO dto) {
		log.info("Atualizando avaliação ID: {}", id);

		Avaliacao avaliacao = avaliacaoRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Avaliação não encontrada com ID: " + id));

		// Validar se usuário autenticado é o autor da avaliação
		Usuario usuario = obterUsuarioAutenticado();
		if (!avaliacao.getUsuario().getId().equals(usuario.getId())) {
			log.warn("Usuário {} tentou atualizar avaliação de outro usuário", usuario.getId());
			throw new BusinessException("Você só pode atualizar suas próprias avaliações");
		}

		avaliacaoMapper.updateEntity(dto, avaliacao);
		Avaliacao avaliacaoAtualizada = avaliacaoRepository.save(avaliacao);

		log.info("Avaliação atualizada com sucesso. ID: {}", id);
		return avaliacaoMapper.toResponseDTO(avaliacaoAtualizada);
	}

	@Override
	@Transactional
	public void deletar(Long id) {
		log.info("Deletando avaliação ID: {}", id);

		Avaliacao avaliacao = avaliacaoRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Avaliação não encontrada com ID: " + id));

		// Validar se usuário autenticado é o autor da avaliação
		Usuario usuario = obterUsuarioAutenticado();
		if (!avaliacao.getUsuario().getId().equals(usuario.getId())) {
			log.warn("Usuário {} tentou deletar avaliação de outro usuário", usuario.getId());
			throw new BusinessException("Você só pode deletar suas próprias avaliações");
		}

		avaliacaoRepository.deleteById(id);
		log.info("Avaliação deletada com sucesso. ID: {}", id);
	}

	@Override
	@Transactional(readOnly = true)
	public AvaliacaoResponseDTO buscarPorId(Long id) {
		log.info("Buscando avaliação com ID: {}", id);
		Avaliacao avaliacao = avaliacaoRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Avaliação não encontrada com ID: " + id));
		return avaliacaoMapper.toResponseDTO(avaliacao);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<AvaliacaoResponseDTO> buscarPorProduto(Long produtoId, Pageable pageable) {
		log.info("Buscando avaliações do produto ID: {}", produtoId);

		// Validar se produto existe
		if (!produtoRepository.existsById(produtoId)) {
			throw new ResourceNotFoundException("Produto não encontrado com ID: " + produtoId);
		}

		return avaliacaoRepository.findAllByProdutoId(produtoId, pageable)
				.map(avaliacaoMapper::toResponseDTO);
	}

	@Override
	@Transactional(readOnly = true)
	public Double calcularMediaPorProduto(Long produtoId) {
		log.info("Calculando média de avaliações do produto ID: {}", produtoId);
		Double media = avaliacaoRepository.calcularMediaPorProduto(produtoId);
		return media != null ? media : 0.0;
	}

	@Override
	@Transactional(readOnly = true)
	public Long contarAvaliacoesPorProduto(Long produtoId) {
		return avaliacaoRepository.contarAvaliacoesPorProduto(produtoId);
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
