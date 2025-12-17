package com.energygames.lojadegames.dto.banner;

import java.time.LocalDateTime;

import com.energygames.lojadegames.enums.TipoBannerEnum;
import com.energygames.lojadegames.model.Banner;

/**
 * DTO de resposta para banners (público)
 */
public class BannerResponseDTO {

    private Long id;
    private String titulo;
    private String subtitulo;
    private String urlImagem;
    private TipoBannerEnum tipo;
    private Long produtoId;
    private String produtoNome;
    private String produtoSlug;
    private String linkCustom;
    private String textoBotao;
    private Integer ordem;
    private Boolean ativo;
    private LocalDateTime dataInicio;
    private LocalDateTime dataFim;

    public BannerResponseDTO() {}

    public static BannerResponseDTO fromEntity(Banner banner) {
        BannerResponseDTO dto = new BannerResponseDTO();
        dto.setId(banner.getId());
        dto.setTitulo(banner.getTitulo());
        dto.setSubtitulo(banner.getSubtitulo());
        dto.setUrlImagem(banner.getUrlImagem());
        dto.setTipo(banner.getTipo());
        dto.setLinkCustom(banner.getLinkCustom());
        dto.setTextoBotao(banner.getTextoBotao());
        dto.setOrdem(banner.getOrdem());
        dto.setAtivo(banner.getAtivo());
        dto.setDataInicio(banner.getDataInicio());
        dto.setDataFim(banner.getDataFim());
        
        if (banner.getProduto() != null) {
            dto.setProdutoId(banner.getProduto().getId());
            dto.setProdutoNome(banner.getProduto().getNome());
            dto.setProdutoSlug(banner.getProduto().getSlug());
        }
        
        return dto;
    }

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

    public String getProdutoSlug() {
        return produtoSlug;
    }

    public void setProdutoSlug(String produtoSlug) {
        this.produtoSlug = produtoSlug;
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
