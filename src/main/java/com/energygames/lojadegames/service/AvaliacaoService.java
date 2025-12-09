package com.energygames.lojadegames.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.energygames.lojadegames.dto.request.AvaliacaoRequestDTO;
import com.energygames.lojadegames.dto.response.AvaliacaoResponseDTO;

public interface AvaliacaoService {

	AvaliacaoResponseDTO criar(AvaliacaoRequestDTO dto);

	AvaliacaoResponseDTO atualizar(Long id, AvaliacaoRequestDTO dto);

	void deletar(Long id);

	AvaliacaoResponseDTO buscarPorId(Long id);

	Page<AvaliacaoResponseDTO> buscarPorProduto(Long produtoId, Pageable pageable);

	Double calcularMediaPorProduto(Long produtoId);

	Long contarAvaliacoesPorProduto(Long produtoId);
}
