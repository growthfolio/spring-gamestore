package com.energygames.lojadegames.dto.mapper;

import org.springframework.stereotype.Component;

import com.energygames.lojadegames.dto.request.CategoriaRequestDTO;
import com.energygames.lojadegames.dto.response.CategoriaResponseDTO;
import com.energygames.lojadegames.model.Categoria;

import java.time.LocalDateTime;

@Component
public class CategoriaMapper {

	public Categoria toEntity(CategoriaRequestDTO dto) {
		Categoria categoria = new Categoria();
		categoria.setTipo(dto.getTipo());
		categoria.setDescricao(dto.getDescricao());
		categoria.setIcone(dto.getIcone());
		categoria.setAtivo(true);
		categoria.setDataCriacao(LocalDateTime.now());
		return categoria;
	}

	public void updateEntity(CategoriaRequestDTO dto, Categoria categoria) {
		categoria.setTipo(dto.getTipo());
		categoria.setDescricao(dto.getDescricao());
		categoria.setIcone(dto.getIcone());
	}

	public CategoriaResponseDTO toResponseDTO(Categoria categoria) {
		CategoriaResponseDTO dto = new CategoriaResponseDTO();
		dto.setId(categoria.getId());
		dto.setTipo(categoria.getTipo());
		dto.setDescricao(categoria.getDescricao());
		dto.setIcone(categoria.getIcone());
		dto.setAtivo(categoria.getAtivo());
		dto.setDataCriacao(categoria.getDataCriacao());
		return dto;
	}
}
