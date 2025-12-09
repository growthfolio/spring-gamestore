package com.energygames.lojadegames.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "tb_carrinho_itens", uniqueConstraints = {
	@UniqueConstraint(columnNames = {"usuario_id", "produto_id"})
})
public class CarrinhoItem {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "usuario_id", nullable = false)
	private Usuario usuario;

	@ManyToOne
	@JoinColumn(name = "produto_id", nullable = false)
	private Produto produto;

	@NotNull(message = "Quantidade é obrigatória")
	@Min(value = 1, message = "Quantidade mínima é 1")
	@Column(nullable = false)
	private Integer quantidade;

	@Column(precision = 10, scale = 2, nullable = false)
	private BigDecimal precoUnitario;

	@Column(precision = 10, scale = 2)
	private BigDecimal descontoUnitario;

	@Column(nullable = false, updatable = false)
	private LocalDateTime dataAdicionado;

	@Column(nullable = false)
	private LocalDateTime dataAtualizacao;

	@PrePersist
	protected void onCreate() {
		this.dataAdicionado = LocalDateTime.now();
		this.dataAtualizacao = LocalDateTime.now();
	}

	@PreUpdate
	protected void onUpdate() {
		this.dataAtualizacao = LocalDateTime.now();
	}

	// Método auxiliar para calcular subtotal
	public BigDecimal calcularSubtotal() {
		BigDecimal precoFinal = precoUnitario;
		
		if (descontoUnitario != null && descontoUnitario.compareTo(BigDecimal.ZERO) > 0) {
			BigDecimal percentualDesconto = descontoUnitario.divide(BigDecimal.valueOf(100), 4, java.math.RoundingMode.HALF_UP);
			BigDecimal valorDesconto = precoUnitario.multiply(percentualDesconto);
			precoFinal = precoUnitario.subtract(valorDesconto);
		}
		
		return precoFinal.multiply(BigDecimal.valueOf(quantidade))
				.setScale(2, java.math.RoundingMode.HALF_UP);
	}

	// Getters e Setters
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public Produto getProduto() {
		return produto;
	}

	public void setProduto(Produto produto) {
		this.produto = produto;
	}

	public Integer getQuantidade() {
		return quantidade;
	}

	public void setQuantidade(Integer quantidade) {
		this.quantidade = quantidade;
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

	public LocalDateTime getDataAdicionado() {
		return dataAdicionado;
	}

	public void setDataAdicionado(LocalDateTime dataAdicionado) {
		this.dataAdicionado = dataAdicionado;
	}

	public LocalDateTime getDataAtualizacao() {
		return dataAtualizacao;
	}

	public void setDataAtualizacao(LocalDateTime dataAtualizacao) {
		this.dataAtualizacao = dataAtualizacao;
	}
}
