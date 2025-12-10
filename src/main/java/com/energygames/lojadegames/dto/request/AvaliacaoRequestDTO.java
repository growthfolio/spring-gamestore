package com.energygames.lojadegames.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class AvaliacaoRequestDTO {

	@NotNull(message = "Nota é obrigatória")
	@Min(value = 1, message = "Nota mínima é 1")
	@Max(value = 5, message = "Nota máxima é 5")
	private Integer nota;

	@Size(max = 500, message = "Comentário não pode ultrapassar 500 caracteres")
	private String comentario;

	@NotNull(message = "ID do produto é obrigatório")
	private Long produtoId;

	// Getters e Setters
	public Integer getNota() {
		return nota;
	}

	public void setNota(Integer nota) {
		this.nota = nota;
	}

	public String getComentario() {
		return comentario;
	}

	public void setComentario(String comentario) {
		this.comentario = comentario;
	}

	public Long getProdutoId() {
		return produtoId;
	}

	public void setProdutoId(Long produtoId) {
		this.produtoId = produtoId;
	}
}
