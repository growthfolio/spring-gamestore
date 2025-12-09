package com.energygames.lojadegames.dto.response;

import java.time.LocalDate;
import java.util.List;

public class ProdutoResponseDTO {

	private Long id;
	private String nome;
	private String descricao;
	private Double preco;
	private Double precoComDesconto;
	private Double desconto;
	private Integer estoque;
	private Boolean emEstoque;
	private String plataforma;
	private String desenvolvedor;
	private String publisher;
	private LocalDate dataLancamento;
	private List<String> imagens;
	private Boolean ativo;
	private CategoriaResumoDTO categoria;

	// Inner class para categoria resumida
	public static class CategoriaResumoDTO {
		private Long id;
		private String tipo;
		private String icone;

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public String getTipo() {
			return tipo;
		}

		public void setTipo(String tipo) {
			this.tipo = tipo;
		}

		public String getIcone() {
			return icone;
		}

		public void setIcone(String icone) {
			this.icone = icone;
		}
	}

	// Getters e Setters
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

	public Double getPreco() {
		return preco;
	}

	public void setPreco(Double preco) {
		this.preco = preco;
	}

	public Double getPrecoComDesconto() {
		return precoComDesconto;
	}

	public void setPrecoComDesconto(Double precoComDesconto) {
		this.precoComDesconto = precoComDesconto;
	}

	public Double getDesconto() {
		return desconto;
	}

	public void setDesconto(Double desconto) {
		this.desconto = desconto;
	}

	public Integer getEstoque() {
		return estoque;
	}

	public void setEstoque(Integer estoque) {
		this.estoque = estoque;
	}

	public Boolean getEmEstoque() {
		return emEstoque;
	}

	public void setEmEstoque(Boolean emEstoque) {
		this.emEstoque = emEstoque;
	}

	public String getPlataforma() {
		return plataforma;
	}

	public void setPlataforma(String plataforma) {
		this.plataforma = plataforma;
	}

	public String getDesenvolvedor() {
		return desenvolvedor;
	}

	public void setDesenvolvedor(String desenvolvedor) {
		this.desenvolvedor = desenvolvedor;
	}

	public String getPublisher() {
		return publisher;
	}

	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}

	public LocalDate getDataLancamento() {
		return dataLancamento;
	}

	public void setDataLancamento(LocalDate dataLancamento) {
		this.dataLancamento = dataLancamento;
	}

	public List<String> getImagens() {
		return imagens;
	}

	public void setImagens(List<String> imagens) {
		this.imagens = imagens;
	}

	public Boolean getAtivo() {
		return ativo;
	}

	public void setAtivo(Boolean ativo) {
		this.ativo = ativo;
	}

	public CategoriaResumoDTO getCategoria() {
		return categoria;
	}

	public void setCategoria(CategoriaResumoDTO categoria) {
		this.categoria = categoria;
	}
}
