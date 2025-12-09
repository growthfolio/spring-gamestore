package com.energygames.lojadegames.service;

import java.util.List;

import com.energygames.lojadegames.dto.request.CategoriaRequestDTO;
import com.energygames.lojadegames.dto.response.CategoriaResponseDTO;

public interface CategoriaService {
	
	List<CategoriaResponseDTO> buscarTodas();
	
	CategoriaResponseDTO buscarPorId(Long id);
	
	List<CategoriaResponseDTO> buscarPorDescricao(String descricao);
	
	CategoriaResponseDTO criar(CategoriaRequestDTO dto);
	
	CategoriaResponseDTO atualizar(Long id, CategoriaRequestDTO dto);
	
	void deletar(Long id);
}
