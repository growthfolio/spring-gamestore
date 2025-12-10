package com.energygames.lojadegames.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class CarrinhoRequestDTO {

	@NotNull(message = "ID do produto é obrigatório")
	private Long produtoId;

	@NotNull(message = "Quantidade é obrigatória")
	@Min(value = 1, message = "Quantidade mínima é 1")
	private Integer quantidade;

	// Getters e Setters
	public Long getProdutoId() {
		return produtoId;
	}

	public void setProdutoId(Long produtoId) {
		this.produtoId = produtoId;
	}

	public Integer getQuantidade() {
		return quantidade;
	}

	public void setQuantidade(Integer quantidade) {
		this.quantidade = quantidade;
	}
}
