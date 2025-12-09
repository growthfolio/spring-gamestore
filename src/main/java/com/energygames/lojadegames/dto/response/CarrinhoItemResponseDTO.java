package com.energygames.lojadegames.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CarrinhoItemResponseDTO {

	private Long id;
	private Long produtoId;
	private String produtoNome;
	private String produtoImagem;
	private String plataforma;
	private BigDecimal precoUnitario;
	private BigDecimal descontoUnitario;
	private BigDecimal precoComDesconto;
	private Integer quantidade;
	private BigDecimal subtotal;
	private Boolean disponivelEstoque;
	private Integer estoqueDisponivel;
	private LocalDateTime dataAdicionado;

	// Getters e Setters
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getProdutoId() {
		return produtoId;
	}

	public void setProdutoId(Long produtoId) {
		this.produtoId = produtoId;
	}

	public String getProdutoNome() {
		return produtoNome;
	}

	public void setProdutoNome(String produtoNome) {
		this.produtoNome = produtoNome;
	}

	public String getProdutoImagem() {
		return produtoImagem;
	}

	public void setProdutoImagem(String produtoImagem) {
		this.produtoImagem = produtoImagem;
	}

	public String getPlataforma() {
		return plataforma;
	}

	public void setPlataforma(String plataforma) {
		this.plataforma = plataforma;
	}

	public BigDecimal getPrecoUnitario() {
		return precoUnitario;
	}

	public void setPrecoUnitario(BigDecimal precoUnitario) {
		this.precoUnitario = precoUnitario;
	}

	public BigDecimal getDescontoUnitario() {
		return descontoUnitario;
	}

	public void setDescontoUnitario(BigDecimal descontoUnitario) {
		this.descontoUnitario = descontoUnitario;
	}

	public BigDecimal getPrecoComDesconto() {
		return precoComDesconto;
	}

	public void setPrecoComDesconto(BigDecimal precoComDesconto) {
		this.precoComDesconto = precoComDesconto;
	}

	public Integer getQuantidade() {
		return quantidade;
	}

	public void setQuantidade(Integer quantidade) {
		this.quantidade = quantidade;
	}

	public BigDecimal getSubtotal() {
		return subtotal;
	}

	public void setSubtotal(BigDecimal subtotal) {
		this.subtotal = subtotal;
	}

	public Boolean getDisponivelEstoque() {
		return disponivelEstoque;
	}

	public void setDisponivelEstoque(Boolean disponivelEstoque) {
		this.disponivelEstoque = disponivelEstoque;
	}

	public Integer getEstoqueDisponivel() {
		return estoqueDisponivel;
	}

	public void setEstoqueDisponivel(Integer estoqueDisponivel) {
		this.estoqueDisponivel = estoqueDisponivel;
	}

	public LocalDateTime getDataAdicionado() {
		return dataAdicionado;
	}

	public void setDataAdicionado(LocalDateTime dataAdicionado) {
		this.dataAdicionado = dataAdicionado;
	}
}
