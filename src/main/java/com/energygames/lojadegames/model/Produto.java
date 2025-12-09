package com.energygames.lojadegames.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "tb_produtos")
public class Produto {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank(message = "O nome do produto é obrigatório!")
	@Size(min = 2, max = 50, message = "O nome deve conter entre 2 e 50 caracteres.")
	private String nome;

	@NotNull(message = "A descrição do produto é obrigatória.")
	@Size(max = 255, message = "A descrição não pode ultrapassar 255 caracteres.")
	private String descricao;

	@NotNull(message = "O preço do produto é obrigatório.")
	@DecimalMin(value = "0.01", message = "Preço deve ser maior que zero")
	@Column(precision = 10, scale = 2)
	private BigDecimal preco;

	@DecimalMin(value = "0.0", message = "Desconto não pode ser negativo")
	@DecimalMax(value = "100.0", message = "Desconto não pode exceder 100%")
	@Column(precision = 5, scale = 2)
	private BigDecimal desconto;

	@NotNull(message = "O estoque do produto é obrigatório.")
	private Integer estoque;

	@NotBlank(message = "A plataforma do produto é obrigatória.")
	private String plataforma; // Ex.: "PlayStation", "Xbox", "PC"

	@NotBlank(message = "O desenvolvedor do produto é obrigatório.")
	private String desenvolvedor;

	@NotBlank(message = "A publisher do produto é obrigatória.")
	private String publisher;

	@NotNull(message = "A data de lançamento é obrigatória.")
	private LocalDate dataLancamento;

	@ElementCollection
	private List<String> imagens; // Lista de URLs para imagens do produto

	@NotNull
	@Column(columnDefinition = "boolean default true")
	private Boolean ativo; // Indica se o produto está disponível para venda

	@ManyToOne
	@JsonIgnoreProperties("produto")
	private Categoria categoria;

	@ManyToOne
	@JsonIgnoreProperties("produto")
	private Usuario usuario;

	@OneToMany(mappedBy = "produto", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JsonIgnoreProperties("produto")
	private List<Avaliacao> avaliacoes;

	// Construtor vazio para JPA
	public Produto() {}

	// Construtor com parâmetros básicos
	public Produto(String nome, String descricao, BigDecimal preco, Integer estoque, String plataforma,
				   String desenvolvedor, String publisher, LocalDate dataLancamento, Categoria categoria) {
		this.nome = nome;
		this.descricao = descricao;
		this.preco = preco;
		this.estoque = estoque;
		this.plataforma = plataforma;
		this.desenvolvedor = desenvolvedor;
		this.publisher = publisher;
		this.dataLancamento = dataLancamento;
		this.categoria = categoria;
		this.ativo = true; // Ativo por padrão
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

	public Boolean getAtivo() {
		return ativo;
	}

	public void setAtivo(Boolean ativo) {
		this.ativo = ativo;
	}

	public Categoria getCategoria() {
		return categoria;
	}

	public void setCategoria(Categoria categoria) {
		this.categoria = categoria;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public List<Avaliacao> getAvaliacoes() {
		return avaliacoes;
	}

	public void setAvaliacoes(List<Avaliacao> avaliacoes) {
		this.avaliacoes = avaliacoes;
	}
}
