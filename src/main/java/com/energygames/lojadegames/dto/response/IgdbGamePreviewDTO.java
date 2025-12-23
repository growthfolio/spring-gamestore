package com.energygames.lojadegames.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

/**
 * DTO de resposta para preview detalhado de jogo da IGDB
 * Fornece informações completas para visualização rápida
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class IgdbGamePreviewDTO {

    private Long igdbId;
    private String nome;
    private String slug;
    private String descricao;
    private String storyline;
    private String dataLancamento;
    private Double rating;
    private Integer ratingCount;
    private String urlCapa;
    private List<String> screenshots;
    private List<String> videos;
    private List<String> plataformas;
    private List<String> generos;
    private List<String> modos;
    private List<String> themes;
    private String desenvolvedores;
    private String publishers;
    private String website;
    private Boolean jaImportado;
    private Long produtoIdLocal;
    private String ageRating;
    private List<String> tags;

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

    public String getStoryline() {
        return storyline;
    }

    public void setStoryline(String storyline) {
        this.storyline = storyline;
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

    public Integer getRatingCount() {
        return ratingCount;
    }

    public void setRatingCount(Integer ratingCount) {
        this.ratingCount = ratingCount;
    }

    public String getUrlCapa() {
        return urlCapa;
    }

    public void setUrlCapa(String urlCapa) {
        this.urlCapa = urlCapa;
    }

    public List<String> getScreenshots() {
        return screenshots;
    }

    public void setScreenshots(List<String> screenshots) {
        this.screenshots = screenshots;
    }

    public List<String> getVideos() {
        return videos;
    }

    public void setVideos(List<String> videos) {
        this.videos = videos;
    }

    public List<String> getPlataformas() {
        return plataformas;
    }

    public void setPlataformas(List<String> plataformas) {
        this.plataformas = plataformas;
    }

    public List<String> getGeneros() {
        return generos;
    }

    public void setGeneros(List<String> generos) {
        this.generos = generos;
    }

    public List<String> getModos() {
        return modos;
    }

    public void setModos(List<String> modos) {
        this.modos = modos;
    }

    public List<String> getThemes() {
        return themes;
    }

    public void setThemes(List<String> themes) {
        this.themes = themes;
    }

    public String getDesenvolvedores() {
        return desenvolvedores;
    }

    public void setDesenvolvedores(String desenvolvedores) {
        this.desenvolvedores = desenvolvedores;
    }

    public String getPublishers() {
        return publishers;
    }

    public void setPublishers(String publishers) {
        this.publishers = publishers;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public Boolean getJaImportado() {
        return jaImportado;
    }

    public void setJaImportado(Boolean jaImportado) {
        this.jaImportado = jaImportado;
    }

    public Long getProdutoIdLocal() {
        return produtoIdLocal;
    }

    public void setProdutoIdLocal(Long produtoIdLocal) {
        this.produtoIdLocal = produtoIdLocal;
    }

    public String getAgeRating() {
        return ageRating;
    }

    public void setAgeRating(String ageRating) {
        this.ageRating = ageRating;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }
}
