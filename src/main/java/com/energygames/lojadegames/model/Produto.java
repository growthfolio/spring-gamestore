package com.energygames.lojadegames.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.energygames.lojadegames.enums.StatusJogoEnum;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "tb_produtos", indexes = {
	@Index(name = "idx_slug", columnList = "slug"),
	@Index(name = "idx_ativo_data", columnList = "ativo,dataLancamento"),
	@Index(name = "idx_status", columnList = "status"),
	@Index(name = "idx_rating", columnList = "ratingIgdb")
})
public class Produto {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank(message = "O nome do produto é obrigatório!")
	@Size(min = 2, max = 100, message = "O nome deve conter entre 2 e 100 caracteres.")
	private String nome;

	@Column(unique = true, length = 100)
	private String slug; // URL amigável gerada automaticamente

	@NotNull(message = "A descrição do produto é obrigatória.")
	@Size(max = 500, message = "A descrição não pode ultrapassar 500 caracteres.")
	private String descricao; // Descrição curta (summary)

	@Column(columnDefinition = "TEXT")
	private String descricaoCompleta; // Descrição completa/storyline (sem limite)

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

	// Novos campos para integração IGDB
	@Column(name = "rating_igdb", precision = 5, scale = 2)
	private BigDecimal ratingIgdb; // Rating da IGDB (0-100)

	@Column(name = "rating_metacritic")
	private Integer ratingMetacritic; // Score do Metacritic (0-100)

	@Column(name = "total_votos_externos")
	private Integer totalVotosExternos; // Total de votos/reviews externos

	@Column(name = "popularidade")
	private Integer popularidade; // Seguidores/interesse na IGDB

	@Enumerated(EnumType.STRING)
	@Column(length = 50)
	private StatusJogoEnum status; // RELEASED, EARLY_ACCESS, BETA, UPCOMING, etc.

	// Campos de auditoria
	@Column(name = "data_criacao", updatable = false)
	private LocalDateTime dataCriacao;

	@Column(name = "data_atualizacao")
	private LocalDateTime dataAtualizacao;

	// Relacionamento legado (manter por compatibilidade)
	@ManyToOne
	@JsonIgnoreProperties("produto")
	private Categoria categoria;

	// Novo relacionamento N:N com gêneros
	@ManyToMany
	@JoinTable(
		name = "tb_produto_genero",
		joinColumns = @JoinColumn(name = "produto_id"),
		inverseJoinColumns = @JoinColumn(name = "genero_id")
	)
	@JsonIgnoreProperties("produtos")
	private Set<Categoria> generos = new HashSet<>();

	// Novo relacionamento N:N com plataformas
	@ManyToMany
	@JoinTable(
		name = "tb_produto_plataforma",
		joinColumns = @JoinColumn(name = "produto_id"),
		inverseJoinColumns = @JoinColumn(name = "plataforma_id")
	)
	@JsonIgnoreProperties("produtos")
	private Set<Plataforma> plataformas = new HashSet<>();

	// Relacionamento com imagens estruturadas
	@OneToMany(mappedBy = "produto", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	@JsonIgnoreProperties("produto")
	private Set<ProdutoImagem> imagensEstruturadas = new HashSet<>();

	// Relacionamento com vídeos
	@OneToMany(mappedBy = "produto", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	@JsonIgnoreProperties("produto")
	private Set<ProdutoVideo> videos = new HashSet<>();

	// Relacionamento com origem externa
	@OneToOne(mappedBy = "produto", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	@JsonIgnoreProperties("produto")
	private ProdutoOrigemExterna origemExterna;

	// Links externos (steam, official website, youtube, etc)
	@ElementCollection
	@CollectionTable(name = "tb_produto_links", joinColumns = @JoinColumn(name = "produto_id"))
	@MapKeyColumn(name = "tipo_link", length = 50)
	@Column(name = "url", length = 500)
	private Map<String, String> linksExternos = new HashMap<>();

	@ManyToOne
	@JsonIgnoreProperties("produto")
	private Usuario usuario;

	@OneToMany(mappedBy = "produto", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JsonIgnoreProperties("produto")
	private List<Avaliacao> avaliacoes;

	// Métodos de ciclo de vida
	@PrePersist
	protected void onCreate() {
		dataCriacao = LocalDateTime.now();
		dataAtualizacao = LocalDateTime.now();
		if (slug == null && nome != null) {
			slug = gerarSlug(nome);
		}
		if (status == null) {
			status = StatusJogoEnum.RELEASED;
		}
	}

	@PreUpdate
	protected void onUpdate() {
		dataAtualizacao = LocalDateTime.now();
	}

	/**
	 * Gera um slug a partir do nome do produto
	 */
	private String gerarSlug(String texto) {
		return texto.toLowerCase()
			.replaceAll("[^a-z0-9\\s-]", "")
			.replaceAll("\\s+", "-")
			.replaceAll("-+", "-")
			.replaceAll("^-|-$", "")
			.trim();
	}

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

	// Getters e Setters dos novos campos

	public String getSlug() {
		return slug;
	}

	public void setSlug(String slug) {
		this.slug = slug;
	}

	public String getDescricaoCompleta() {
		return descricaoCompleta;
	}

	public void setDescricaoCompleta(String descricaoCompleta) {
		this.descricaoCompleta = descricaoCompleta;
	}

	public BigDecimal getRatingIgdb() {
		return ratingIgdb;
	}

	public void setRatingIgdb(BigDecimal ratingIgdb) {
		this.ratingIgdb = ratingIgdb;
	}

	public Integer getRatingMetacritic() {
		return ratingMetacritic;
	}

	public void setRatingMetacritic(Integer ratingMetacritic) {
		this.ratingMetacritic = ratingMetacritic;
	}

	public Integer getTotalVotosExternos() {
		return totalVotosExternos;
	}

	public void setTotalVotosExternos(Integer totalVotosExternos) {
		this.totalVotosExternos = totalVotosExternos;
	}

	public Integer getPopularidade() {
		return popularidade;
	}

	public void setPopularidade(Integer popularidade) {
		this.popularidade = popularidade;
	}

	public StatusJogoEnum getStatus() {
		return status;
	}

	public void setStatus(StatusJogoEnum status) {
		this.status = status;
	}

	public LocalDateTime getDataCriacao() {
		return dataCriacao;
	}

	public void setDataCriacao(LocalDateTime dataCriacao) {
		this.dataCriacao = dataCriacao;
	}

	public LocalDateTime getDataAtualizacao() {
		return dataAtualizacao;
	}

	public void setDataAtualizacao(LocalDateTime dataAtualizacao) {
		this.dataAtualizacao = dataAtualizacao;
	}

	public Set<Categoria> getGeneros() {
		return generos;
	}

	public void setGeneros(Set<Categoria> generos) {
		this.generos = generos;
	}

	public Set<Plataforma> getPlataformas() {
		return plataformas;
	}

	public void setPlataformas(Set<Plataforma> plataformas) {
		this.plataformas = plataformas;
	}

	public Set<ProdutoImagem> getImagensEstruturadas() {
		return imagensEstruturadas;
	}

	public void setImagensEstruturadas(Set<ProdutoImagem> imagensEstruturadas) {
		this.imagensEstruturadas = imagensEstruturadas;
	}

	public Set<ProdutoVideo> getVideos() {
		return videos;
	}

	public void setVideos(Set<ProdutoVideo> videos) {
		this.videos = videos;
	}

	public ProdutoOrigemExterna getOrigemExterna() {
		return origemExterna;
	}

	public void setOrigemExterna(ProdutoOrigemExterna origemExterna) {
		this.origemExterna = origemExterna;
	}

	public Map<String, String> getLinksExternos() {
		return linksExternos;
	}

	public void setLinksExternos(Map<String, String> linksExternos) {
		this.linksExternos = linksExternos;
	}

	// Métodos auxiliares

	/**
	 * Adiciona uma plataforma ao produto
	 */
	public void adicionarPlataforma(Plataforma plataforma) {
		this.plataformas.add(plataforma);
		plataforma.getProdutos().add(this);
	}

	/**
	 * Remove uma plataforma do produto
	 */
	public void removerPlataforma(Plataforma plataforma) {
		this.plataformas.remove(plataforma);
		plataforma.getProdutos().remove(this);
	}

	/**
	 * Adiciona um gênero ao produto
	 */
	public void adicionarGenero(Categoria genero) {
		this.generos.add(genero);
		genero.getProdutos().add(this);
	}

	/**
	 * Remove um gênero do produto
	 */
	public void removerGenero(Categoria genero) {
		this.generos.remove(genero);
		genero.getProdutos().remove(this);
	}

	/**
	 * Adiciona uma imagem ao produto
	 */
	public void adicionarImagem(ProdutoImagem imagem) {
		imagensEstruturadas.add(imagem);
		imagem.setProduto(this);
	}

	/**
	 * Adiciona um vídeo ao produto
	 */
	public void adicionarVideo(ProdutoVideo video) {
		videos.add(video);
		video.setProduto(this);
	}

	/**
	 * Adiciona um link externo
	 */
	public void adicionarLinkExterno(String tipo, String url) {
		linksExternos.put(tipo, url);
	}

	/**
	 * Retorna o preço com desconto aplicado
	 */
	public BigDecimal getPrecoComDesconto() {
		if (desconto == null || desconto.compareTo(BigDecimal.ZERO) == 0) {
			return preco;
		}
		BigDecimal percentualDesconto = desconto.divide(new BigDecimal("100"));
		BigDecimal valorDesconto = preco.multiply(percentualDesconto);
		return preco.subtract(valorDesconto);
	}

	/**
	 * Verifica se o produto está disponível para venda
	 */
	public boolean isDisponivelParaVenda() {
		return ativo && estoque > 0 && (status == StatusJogoEnum.RELEASED || status == StatusJogoEnum.EARLY_ACCESS);
	}
}
