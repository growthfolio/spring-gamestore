package com.energygames.lojadegames.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * DTO de resposta para resultados de busca na IGDB
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class IgdbSearchResultDTO {

    private Long igdbId;
    private String nome;
    private String slug;
    private String descricao;
    private String dataLancamento;
    private Double rating;
    private String urlCapa;
    private java.util.List<String> plataformas;
    private java.util.List<String> generos;
    private boolean jaImportado;
    private Long produtoIdLocal;

    // Getters e Setters

    public Long getIgdbId() {
        return igdbId;
    }

    public void setIgdbId(Long igdbId) {
        this.igdbId = igdbId;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getDataLancamento() {
        return dataLancamento;
    }

    public void setDataLancamento(String dataLancamento) {
        this.dataLancamento = dataLancamento;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public String getUrlCapa() {
        return urlCapa;
    }

    public void setUrlCapa(String urlCapa) {
        this.urlCapa = urlCapa;
    }

    public boolean isJaImportado() {
        return jaImportado;
    }

    public void setJaImportado(boolean jaImportado) {
        this.jaImportado = jaImportado;
    }

    public Long getProdutoIdLocal() {
        return produtoIdLocal;
    }

    public void setProdutoIdLocal(Long produtoIdLocal) {
        this.produtoIdLocal = produtoIdLocal;
    }

    public java.util.List<String> getPlataformas() {
        return plataformas;
    }

    public void setPlataformas(java.util.List<String> plataformas) {
        this.plataformas = plataformas;
    }

    public java.util.List<String> getGeneros() {
        return generos;
    }

    public void setGeneros(java.util.List<String> generos) {
        this.generos = generos;
    }
}
