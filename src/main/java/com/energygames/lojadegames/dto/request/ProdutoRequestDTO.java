package com.energygames.lojadegames.dto.request;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

public class ProdutoRequestDTO {

	@NotBlank(message = "Nome é obrigatório")
	@Size(min = 2, max = 50, message = "Nome deve ter entre 2 e 50 caracteres")
	private String nome;

	@NotBlank(message = "Descrição é obrigatória")
	@Size(min = 10, max = 255, message = "Descrição deve ter entre 10 e 255 caracteres")
	private String descricao;

	@NotNull(message = "Preço é obrigatório")
	@DecimalMin(value = "0.01", message = "Preço deve ser maior que zero")
	@DecimalMax(value = "99999.99", message = "Preço não pode exceder 99999.99")
	private BigDecimal preco;

	@DecimalMin(value = "0.0", message = "Desconto não pode ser negativo")
	@DecimalMax(value = "100.0", message = "Desconto não pode exceder 100%")
	private BigDecimal desconto;

	@NotNull(message = "Estoque é obrigatório")
	@Min(value = 0, message = "Estoque não pode ser negativo")
	private Integer estoque;

	@NotBlank(message = "Plataforma é obrigatória")
	@Size(min = 2, max = 30, message = "Plataforma deve ter entre 2 e 30 caracteres")
	private String plataforma;

	@NotBlank(message = "Desenvolvedor é obrigatório")
	@Size(min = 2, max = 50, message = "Desenvolvedor deve ter entre 2 e 50 caracteres")
	private String desenvolvedor;

	@NotBlank(message = "Publisher é obrigatório")
	@Size(min = 2, max = 50, message = "Publisher deve ter entre 2 e 50 caracteres")
	private String publisher;

	@NotNull(message = "Data de lançamento é obrigatória")
	@PastOrPresent(message = "Data de lançamento não pode ser futura")
	private LocalDate dataLancamento;

	private List<String> imagens;

	@NotNull(message = "Categoria é obrigatória")
	private Long categoriaId;

	// Getters e Setters
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

	public BigDecimal getPreco() {
		return preco;
	}

	public void setPreco(BigDecimal preco) {
		this.preco = preco;
	}

	public BigDecimal getDesconto() {
		return desconto;
	}

	public void setDesconto(BigDecimal desconto) {
		this.desconto = desconto;
	}

	public Integer getEstoque() {
		return estoque;
	}

	public void setEstoque(Integer estoque) {
		this.estoque = estoque;
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

	public Long getCategoriaId() {
		return categoriaId;
	}

	public void setCategoriaId(Long categoriaId) {
		this.categoriaId = categoriaId;
	}
}
