package com.energygames.lojadegames.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CategoriaRequestDTO {

	@NotBlank(message = "Tipo é obrigatório")
	@Size(min = 3, max = 50, message = "Tipo deve ter entre 3 e 50 caracteres")
	private String tipo;

	@Size(max = 255, message = "Descrição não pode ultrapassar 255 caracteres")
	private String descricao;

	private String icone;

	// Getters e Setters
	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getIcone() {
		return icone;
	}

	public void setIcone(String icone) {
		this.icone = icone;
	}
}
