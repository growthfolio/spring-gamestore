package com.energygames.lojadegames.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.energygames.lojadegames.dto.mapper.FavoritoMapper;
import com.energygames.lojadegames.dto.response.FavoritoResponseDTO;
import com.energygames.lojadegames.exception.BusinessException;
import com.energygames.lojadegames.exception.ResourceNotFoundException;
import com.energygames.lojadegames.model.Favorito;
import com.energygames.lojadegames.model.Produto;
import com.energygames.lojadegames.model.Usuario;
import com.energygames.lojadegames.repository.FavoritoRepository;
import com.energygames.lojadegames.repository.ProdutoRepository;
import com.energygames.lojadegames.repository.UsuarioRepository;
import com.energygames.lojadegames.service.FavoritoService;

@Service
public class FavoritoServiceImpl implements FavoritoService {

	private static final Logger log = LoggerFactory.getLogger(FavoritoServiceImpl.class);

	private final FavoritoRepository favoritoRepository;
	private final ProdutoRepository produtoRepository;
	private final UsuarioRepository usuarioRepository;
	private final FavoritoMapper favoritoMapper;

	public FavoritoServiceImpl(FavoritoRepository favoritoRepository, ProdutoRepository produtoRepository,
			UsuarioRepository usuarioRepository, FavoritoMapper favoritoMapper) {
		this.favoritoRepository = favoritoRepository;
		this.produtoRepository = produtoRepository;
		this.usuarioRepository = usuarioRepository;
		this.favoritoMapper = favoritoMapper;
	}

	@Override
	@Transactional
	public FavoritoResponseDTO adicionar(Long produtoId) {
		log.info("Adicionando produto {} aos favoritos", produtoId);

		Usuario usuario = obterUsuarioAutenticado();

		// Validar se produto existe
		Produto produto = produtoRepository.findById(produtoId)
				.orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado com ID: " + produtoId));

		// Validar se produto está ativo
		if (produto.getAtivo() == null || !produto.getAtivo()) {
			log.warn("Tentativa de favoritar produto inativo. Produto ID: {}", produtoId);
			throw new BusinessException("Produto inativo não pode ser favoritado");
		}

		// Validar se já está nos favoritos
		if (favoritoRepository.existsByUsuarioIdAndProdutoId(usuario.getId(), produtoId)) {
			log.warn("Produto {} já está nos favoritos do usuário {}", produtoId, usuario.getId());
			throw new BusinessException("Este produto já está nos seus favoritos");
		}

		Favorito favorito = new Favorito();
		favorito.setUsuario(usuario);
		favorito.setProduto(produto);

		Favorito favoritoSalvo = favoritoRepository.save(favorito);

		log.info("Produto {} adicionado aos favoritos com sucesso. Favorito ID: {}", produtoId, favoritoSalvo.getId());
		return favoritoMapper.toResponseDTO(favoritoSalvo);
	}

	@Override
	@Transactional
	public void remover(Long produtoId) {
		log.info("Removendo produto {} dos favoritos", produtoId);

		Usuario usuario = obterUsuarioAutenticado();

		// Validar se está nos favoritos
		if (!favoritoRepository.existsByUsuarioIdAndProdutoId(usuario.getId(), produtoId)) {
			log.warn("Produto {} não está nos favoritos do usuário {}", produtoId, usuario.getId());
			throw new ResourceNotFoundException("Este produto não está nos seus favoritos");
		}

		favoritoRepository.deleteByUsuarioIdAndProdutoId(usuario.getId(), produtoId);
		log.info("Produto {} removido dos favoritos com sucesso", produtoId);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<FavoritoResponseDTO> listarMeusFavoritos(Pageable pageable) {
		log.info("Listando favoritos do usuário autenticado");

		Usuario usuario = obterUsuarioAutenticado();

		return favoritoRepository.findAllByUsuarioId(usuario.getId(), pageable)
				.map(favoritoMapper::toResponseDTO);
	}

	@Override
	@Transactional(readOnly = true)
	public boolean isFavorito(Long produtoId) {
		Usuario usuario = obterUsuarioAutenticado();
		return favoritoRepository.existsByUsuarioIdAndProdutoId(usuario.getId(), produtoId);
	}

	@Override
	@Transactional(readOnly = true)
	public Long contarMeusFavoritos() {
		Usuario usuario = obterUsuarioAutenticado();
		return favoritoRepository.contarFavoritosPorUsuario(usuario.getId());
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
