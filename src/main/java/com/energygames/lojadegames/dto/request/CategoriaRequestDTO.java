package com.energygames.lojadegames.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class CategoriaRequestDTO {

	@NotBlank(message = "Tipo é obrigatório")
	@Size(min = 3, max = 50, message = "Tipo deve ter entre 3 e 50 caracteres")
	@Pattern(regexp = "^[A-Za-zÀ-ÿ\\s]+$", message = "Tipo deve conter apenas letras")
	private String tipo;

	@NotBlank(message = "Descrição é obrigatória")
	@Size(min = 10, max = 255, message = "Descrição deve ter entre 10 e 255 caracteres")
	private String descricao;

	@Size(max = 100, message = "URL do ícone não pode ultrapassar 100 caracteres")
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
