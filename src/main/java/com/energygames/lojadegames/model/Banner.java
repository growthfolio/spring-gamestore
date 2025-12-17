package com.energygames.lojadegames.model;

import java.time.LocalDateTime;

import com.energygames.lojadegames.enums.TipoBannerEnum;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Entidade para banners do carousel da home page.
 * Permite ao admin definir quais produtos/imagens aparecem no carousel principal.
 */
@Entity
@Table(name = "banners")
public class Banner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O título é obrigatório")
    @Size(max = 100, message = "O título deve ter no máximo 100 caracteres")
    @Column(nullable = false, length = 100)
    private String titulo;

    @Size(max = 200, message = "O subtítulo deve ter no máximo 200 caracteres")
    @Column(length = 200)
    private String subtitulo;

    /**
     * URL da imagem do banner.
     * Pode ser uma URL externa ou uma URL de artwork/screenshot do produto vinculado.
     */
    @NotBlank(message = "A URL da imagem é obrigatória")
    @Column(name = "url_imagem", nullable = false, length = 500)
    private String urlImagem;

    /**
     * Tipo do banner: PRODUTO (vinculado a um produto) ou CUSTOM (imagem personalizada)
     */
    @NotNull(message = "O tipo do banner é obrigatório")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoBannerEnum tipo;

    /**
     * Produto vinculado (opcional).
     * Se definido, o botão do banner leva para a página do produto.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "produto_id")
    private Produto produto;

    /**
     * Link customizado (opcional).
     * Usado quando tipo = CUSTOM ou quando quer redirecionar para outra página.
     */
    @Column(name = "link_custom", length = 500)
    private String linkCustom;

    /**
     * Texto do botão de ação (ex: "Comprar agora", "Ver mais")
     */
    @Column(name = "texto_botao", length = 50)
    private String textoBotao;

    /**
     * Ordem de exibição no carousel (menor = primeiro)
     */
    @Column(nullable = false)
    private Integer ordem = 0;

    /**
     * Se o banner está ativo (visível no carousel)
     */
    @Column(nullable = false)
    private Boolean ativo = true;

    /**
     * Data de início da exibição (opcional)
     */
    @Column(name = "data_inicio")
    private LocalDateTime dataInicio;

    /**
     * Data de fim da exibição (opcional)
     */
    @Column(name = "data_fim")
    private LocalDateTime dataFim;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Getters e Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getSubtitulo() {
        return subtitulo;
    }

    public void setSubtitulo(String subtitulo) {
        this.subtitulo = subtitulo;
    }

    public String getUrlImagem() {
        return urlImagem;
    }

    public void setUrlImagem(String urlImagem) {
        this.urlImagem = urlImagem;
    }

    public TipoBannerEnum getTipo() {
        return tipo;
    }

    public void setTipo(TipoBannerEnum tipo) {
        this.tipo = tipo;
    }

    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
    }

    public String getLinkCustom() {
        return linkCustom;
    }

    public void setLinkCustom(String linkCustom) {
        this.linkCustom = linkCustom;
    }

    public String getTextoBotao() {
        return textoBotao;
    }

    public void setTextoBotao(String textoBotao) {
        this.textoBotao = textoBotao;
    }

    public Integer getOrdem() {
        return ordem;
    }

    public void setOrdem(Integer ordem) {
        this.ordem = ordem;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public LocalDateTime getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(LocalDateTime dataInicio) {
        this.dataInicio = dataInicio;
    }

    public LocalDateTime getDataFim() {
        return dataFim;
    }

    public void setDataFim(LocalDateTime dataFim) {
        this.dataFim = dataFim;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @jakarta.persistence.PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @jakarta.persistence.PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
