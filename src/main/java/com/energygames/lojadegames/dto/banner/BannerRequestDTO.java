package com.energygames.lojadegames.dto.banner;

import java.time.LocalDateTime;

import com.energygames.lojadegames.enums.TipoBannerEnum;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * DTO para criação/atualização de banners
 */
public class BannerRequestDTO {

    @NotBlank(message = "O título é obrigatório")
    @Size(max = 100, message = "O título deve ter no máximo 100 caracteres")
    private String titulo;

    @Size(max = 200, message = "O subtítulo deve ter no máximo 200 caracteres")
    private String subtitulo;

    @NotBlank(message = "A URL da imagem é obrigatória")
    private String urlImagem;

    @NotNull(message = "O tipo do banner é obrigatório")
    private TipoBannerEnum tipo;

    /**
     * ID do produto vinculado (opcional, usado quando tipo = PRODUTO)
     */
    private Long produtoId;

    /**
     * Link customizado (opcional)
     */
    private String linkCustom;

    /**
     * Texto do botão de ação
     */
    @Size(max = 50)
    private String textoBotao;

    private Integer ordem = 0;

    private Boolean ativo = true;

    private LocalDateTime dataInicio;

    private LocalDateTime dataFim;

    // Getters e Setters

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

    public Long getProdutoId() {
        return produtoId;
    }

    public void setProdutoId(Long produtoId) {
        this.produtoId = produtoId;
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
}
