package com.energygames.lojadegames.dto.mapper;

import org.springframework.stereotype.Component;

import com.energygames.lojadegames.dto.request.AvaliacaoRequestDTO;
import com.energygames.lojadegames.dto.response.AvaliacaoResponseDTO;
import com.energygames.lojadegames.model.Avaliacao;
import com.energygames.lojadegames.model.Produto;
import com.energygames.lojadegames.model.Usuario;

@Component
public class AvaliacaoMapper {

	public Avaliacao toEntity(AvaliacaoRequestDTO dto, Produto produto, Usuario usuario) {
		Avaliacao avaliacao = new Avaliacao();
		avaliacao.setNota(dto.getNota());
		avaliacao.setComentario(dto.getComentario());
		avaliacao.setProduto(produto);
		avaliacao.setUsuario(usuario);
		return avaliacao;
	}

	public AvaliacaoResponseDTO toResponseDTO(Avaliacao avaliacao) {
		AvaliacaoResponseDTO dto = new AvaliacaoResponseDTO();
		dto.setId(avaliacao.getId());
		dto.setNota(avaliacao.getNota());
		dto.setComentario(avaliacao.getComentario());
		dto.setDataAvaliacao(avaliacao.getDataAvaliacao());
		dto.setProdutoId(avaliacao.getProduto().getId());
		dto.setProdutoNome(avaliacao.getProduto().getNome());
		dto.setUsuarioId(avaliacao.getUsuario().getId());
		dto.setUsuarioNome(avaliacao.getUsuario().getNome());
		return dto;
	}

	public void updateEntity(AvaliacaoRequestDTO dto, Avaliacao avaliacao) {
		avaliacao.setNota(dto.getNota());
		avaliacao.setComentario(dto.getComentario());
	}
}
