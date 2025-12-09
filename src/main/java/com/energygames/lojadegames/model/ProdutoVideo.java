package com.energygames.lojadegames.model;

import com.energygames.lojadegames.enums.TipoVideoEnum;

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
import jakarta.validation.constraints.Size;

/**
 * Entidade que representa um vídeo (trailer, gameplay, etc) de um produto.
 * Armazena principalmente vídeos do YouTube.
 */
@Entity
@Table(name = "tb_produto_videos")
public class ProdutoVideo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "produto_id", nullable = false)
    private Produto produto;

    @NotBlank(message = "ID do vídeo é obrigatório")
    @Size(max = 100, message = "ID do vídeo não pode ultrapassar 100 caracteres")
    @Column(name = "video_id", nullable = false, length = 100)
    private String videoId; // YouTube video ID

    @Size(max = 255, message = "Título não pode ultrapassar 255 caracteres")
    @Column(length = 255)
    private String titulo;

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private TipoVideoEnum tipo; // TRAILER, GAMEPLAY, REVIEW, etc.

    @Column(name = "ordem")
    private Integer ordem; // Para ordenação dos vídeos

    // Construtores
    public ProdutoVideo() {
    }

    public ProdutoVideo(Produto produto, String videoId, TipoVideoEnum tipo) {
        this.produto = produto;
        this.videoId = videoId;
        this.tipo = tipo;
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

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public TipoVideoEnum getTipo() {
        return tipo;
    }

    public void setTipo(TipoVideoEnum tipo) {
        this.tipo = tipo;
    }

    public Integer getOrdem() {
        return ordem;
    }

    public void setOrdem(Integer ordem) {
        this.ordem = ordem;
    }

    /**
     * Retorna a URL completa do vídeo no YouTube
     */
    public String getUrlYoutube() {
        return "https://www.youtube.com/watch?v=" + videoId;
    }

    /**
     * Retorna a URL para embed do vídeo
     */
    public String getUrlEmbed() {
        return "https://www.youtube.com/embed/" + videoId;
    }

    /**
     * Retorna a URL da thumbnail do vídeo
     */
    public String getUrlThumbnail() {
        return "https://img.youtube.com/vi/" + videoId + "/maxresdefault.jpg";
    }
}
