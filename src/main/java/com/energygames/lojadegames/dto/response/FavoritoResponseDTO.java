package com.energygames.lojadegames.dto.response;

import java.time.LocalDateTime;

public class FavoritoResponseDTO {

	private Long id;
	private LocalDateTime dataAdicionado;
	private ProdutoResumoDTO produto;

	// Inner class para produto resumido
	public static class ProdutoResumoDTO {
		private Long id;
		private String nome;
		private String descricao;
		private java.math.BigDecimal preco;
		private java.math.BigDecimal precoComDesconto;
		private String plataforma;
		private Boolean emEstoque;
		private String imagemPrincipal;

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public String getNome() {
			return nome;
		}

		public void setNome(String nome) {
			this.nome = nome;
		}

		public String getDescricao() {
			return descricao;
		}

		public void setDescricao(String descricao) {
			this.descricao = descricao;
		}

		public java.math.BigDecimal getPreco() {
			return preco;
		}

		public void setPreco(java.math.BigDecimal preco) {
			this.preco = preco;
		}

		public java.math.BigDecimal getPrecoComDesconto() {
			return precoComDesconto;
		}

		public void setPrecoComDesconto(java.math.BigDecimal precoComDesconto) {
			this.precoComDesconto = precoComDesconto;
		}

		public String getPlataforma() {
			return plataforma;
		}

		public void setPlataforma(String plataforma) {
			this.plataforma = plataforma;
		}

		public Boolean getEmEstoque() {
			return emEstoque;
		}

		public void setEmEstoque(Boolean emEstoque) {
			this.emEstoque = emEstoque;
		}

		public String getImagemPrincipal() {
			return imagemPrincipal;
		}

		public void setImagemPrincipal(String imagemPrincipal) {
			this.imagemPrincipal = imagemPrincipal;
		}
	}

	// Getters e Setters
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public LocalDateTime getDataAdicionado() {
		return dataAdicionado;
	}

	public void setDataAdicionado(LocalDateTime dataAdicionado) {
		this.dataAdicionado = dataAdicionado;
	}

	public ProdutoResumoDTO getProduto() {
		return produto;
	}

	public void setProduto(ProdutoResumoDTO produto) {
		this.produto = produto;
	}
}
