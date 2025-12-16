package com.energygames.lojadegames.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.energygames.lojadegames.dto.request.ProdutoComercialUpdateDTO;
import com.energygames.lojadegames.dto.request.ProdutoRequestDTO;
import com.energygames.lojadegames.dto.response.ProdutoDetalheResponseDTO;
import com.energygames.lojadegames.dto.response.ProdutoResponseDTO;

public interface ProdutoService {
	
	Page<ProdutoResponseDTO> buscarTodos(String nome, Long categoriaId, Pageable pageable);
	
	ProdutoResponseDTO buscarPorId(Long id);
	
	/**
	 * Busca produto com mídia estruturada (imagens em múltiplos tamanhos)
	 */
	ProdutoDetalheResponseDTO buscarDetalhePorId(Long id);
	
	/**
	 * Busca produto por slug com mídia estruturada
	 */
	ProdutoDetalheResponseDTO buscarDetalhePorSlug(String slug);
	
	ProdutoResponseDTO criar(ProdutoRequestDTO dto);
	
	ProdutoResponseDTO atualizar(Long id, ProdutoRequestDTO dto);
	
	void deletar(Long id);
	
	ProdutoResponseDTO atualizarDadosComerciais(Long id, ProdutoComercialUpdateDTO dto);

	Page<ProdutoResponseDTO> buscarProdutosPendentes(Pageable pageable);
}
