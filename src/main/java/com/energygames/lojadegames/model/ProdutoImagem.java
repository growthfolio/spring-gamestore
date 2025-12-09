package com.energygames.lojadegames.model;

import com.energygames.lojadegames.enums.TipoImagemEnum;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
 * Entidade que representa uma imagem de um produto.
 * Permite múltiplas imagens com diferentes tipos e ordenação.
 */
@Entity
@Table(name = "tb_produto_imagens")
public class ProdutoImagem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "produto_id", nullable = false)
    private Produto produto;

    @NotBlank(message = "URL da imagem é obrigatória")
    @Size(max = 500, message = "URL não pode ultrapassar 500 caracteres")
    @Column(nullable = false, length = 500)
    private String url;

    @NotNull(message = "Tipo da imagem é obrigatório")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private TipoImagemEnum tipo; // CAPA, SCREENSHOT, ARTWORK, LOGO, etc.

    @Column(name = "ordem")
    private Integer ordem; // Para ordenação das imagens

    @NotNull
    @Column(name = "imagem_principal", columnDefinition = "boolean default false")
    private Boolean imagemPrincipal = false;

    @Column(name = "largura")
    private Integer largura;

    @Column(name = "altura")
    private Integer altura;

    @Column(name = "id_igdb", length = 100)
    private String idIgdb; // image_id da IGDB

    // Construtores
    public ProdutoImagem() {
    }

    public ProdutoImagem(Produto produto, String url, TipoImagemEnum tipo) {
        this.produto = produto;
        this.url = url;
        this.tipo = tipo;
        this.imagemPrincipal = false;
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public TipoImagemEnum getTipo() {
        return tipo;
    }

    public void setTipo(TipoImagemEnum tipo) {
        this.tipo = tipo;
    }

    public Integer getOrdem() {
        return ordem;
    }

    public void setOrdem(Integer ordem) {
        this.ordem = ordem;
    }

    public Boolean getImagemPrincipal() {
        return imagemPrincipal;
    }

    public void setImagemPrincipal(Boolean imagemPrincipal) {
        this.imagemPrincipal = imagemPrincipal;
    }

    public Integer getLargura() {
        return largura;
    }

    public void setLargura(Integer largura) {
        this.largura = largura;
    }

    public Integer getAltura() {
        return altura;
    }

    public void setAltura(Integer altura) {
        this.altura = altura;
    }

    public String getIdIgdb() {
        return idIgdb;
    }

    public void setIdIgdb(String idIgdb) {
        this.idIgdb = idIgdb;
    }
}
