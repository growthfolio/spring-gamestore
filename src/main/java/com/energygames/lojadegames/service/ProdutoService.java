package com.energygames.lojadegames.service;

import java.util.List;

import com.energygames.lojadegames.dto.request.ProdutoRequestDTO;
import com.energygames.lojadegames.dto.response.ProdutoResponseDTO;

public interface ProdutoService {
	
	List<ProdutoResponseDTO> buscarTodos();
	
	ProdutoResponseDTO buscarPorId(Long id);
	
	List<ProdutoResponseDTO> buscarPorNome(String nome);
	
	ProdutoResponseDTO criar(ProdutoRequestDTO dto);
	
	ProdutoResponseDTO atualizar(Long id, ProdutoRequestDTO dto);
	
	void deletar(Long id);
}
