package com.energygames.lojadegames.dto.response;

import java.math.BigDecimal;
import java.util.List;

public class CarrinhoResumoResponseDTO {

	private List<CarrinhoItemResponseDTO> itens;
	private Integer totalItens;
	private Integer totalProdutos;
	private BigDecimal subtotal;
	private BigDecimal descontoTotal;
	private BigDecimal total;

	// Getters e Setters
	public List<CarrinhoItemResponseDTO> getItens() {
		return itens;
	}

	public void setItens(List<CarrinhoItemResponseDTO> itens) {
		this.itens = itens;
	}

	public Integer getTotalItens() {
		return totalItens;
	}

	public void setTotalItens(Integer totalItens) {
		this.totalItens = totalItens;
	}

	public Integer getTotalProdutos() {
		return totalProdutos;
	}

	public void setTotalProdutos(Integer totalProdutos) {
		this.totalProdutos = totalProdutos;
	}

	public BigDecimal getSubtotal() {
		return subtotal;
	}

	public void setSubtotal(BigDecimal subtotal) {
		this.subtotal = subtotal;
	}

	public BigDecimal getDescontoTotal() {
		return descontoTotal;
	}

	public void setDescontoTotal(BigDecimal descontoTotal) {
		this.descontoTotal = descontoTotal;
	}

	public BigDecimal getTotal() {
		return total;
	}

	public void setTotal(BigDecimal total) {
		this.total = total;
	}
}
