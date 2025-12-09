package com.energygames.lojadegames.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.energygames.lojadegames.dto.request.CategoriaRequestDTO;
import com.energygames.lojadegames.dto.response.CategoriaResponseDTO;

public interface CategoriaService {
	
	Page<CategoriaResponseDTO> buscarTodas(String descricao, Pageable pageable);
	
	CategoriaResponseDTO buscarPorId(Long id);
	
	CategoriaResponseDTO criar(CategoriaRequestDTO dto);
	
	CategoriaResponseDTO atualizar(Long id, CategoriaRequestDTO dto);
	
	void deletar(Long id);
}
