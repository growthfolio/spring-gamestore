package com.energygames.lojadegames.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.energygames.lojadegames.dto.response.FavoritoResponseDTO;

public interface FavoritoService {

	FavoritoResponseDTO adicionar(Long produtoId);

	void remover(Long produtoId);

	Page<FavoritoResponseDTO> listarMeusFavoritos(Pageable pageable);

	boolean isFavorito(Long produtoId);

	Long contarMeusFavoritos();
}
