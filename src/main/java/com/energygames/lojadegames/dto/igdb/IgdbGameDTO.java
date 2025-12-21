package com.energygames.lojadegames.dto.igdb;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO para resposta da API IGDB - Endpoint /games
 * Documenta campos retornados pela IGDB API v4
 * Ref: https://api-docs.igdb.com/#game
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class IgdbGameDTO {

    private Long id;
    private String name;
    private String slug;
    private String summary; // descrição curta
    @JsonProperty("storyline")
    private String storyline; // descrição completa

    @JsonProperty("first_release_date")
    private Long firstReleaseDate; // Unix timestamp

    @JsonProperty("aggregated_rating")
    private BigDecimal aggregatedRating; // média de ratings (0-100)
    
    @JsonProperty("aggregated_rating_count")
    private Integer aggregatedRatingCount;
    
    @JsonProperty("rating")
    private BigDecimal rating; // rating IGDB (0-100)
    
    @JsonProperty("rating_count")
    private Integer ratingCount;
    
    @JsonProperty("total_rating")
    private BigDecimal totalRating; // rating médio geral (0-100)
    
    @JsonProperty("total_rating_count")
    private Integer totalRatingCount;

    private IgdbCoverDTO cover; // Objeto da capa (expandido)
    
    @JsonProperty("screenshots")
    @JsonDeserialize(using = FlexibleLongListDeserializer.class)
    private List<Long> screenshots; // IDs dos screenshots (precisa buscar em /screenshots)
    
    @JsonProperty("artworks")
    @JsonDeserialize(using = FlexibleLongListDeserializer.class)
    private List<Long> artworks; // IDs das artworks/imagens promocionais (precisa buscar em /artworks)
    
    @JsonProperty("videos")
    @JsonDeserialize(using = FlexibleLongListDeserializer.class)
    private List<Long> videos; // IDs dos vídeos (precisa buscar em /game_videos)
    
    private List<IgdbPlatformDTO> platforms; // Objetos das plataformas (expandido)
    
    private List<IgdbGenreDTO> genres; // Objetos dos gêneros (expandido)
    
    @JsonProperty("involved_companies")
    @JsonDeserialize(using = FlexibleLongListDeserializer.class)
    private List<Long> involvedCompanies; // IDs das empresas

    private Integer status; // Status: 0=Released, 2=Alpha, 3=Beta, 4=Early Access, 5=Offline, 6=Cancelled, 7=Rumored, 8=Delisted

    @JsonProperty("external_games")
    @JsonDeserialize(using = FlexibleLongListDeserializer.class)
    private List<Long> externalGames; // IDs dos links externos (Steam, Epic, etc)

    @JsonProperty("websites")
    @JsonDeserialize(using = FlexibleLongListDeserializer.class)
    private List<Long> websites; // IDs dos sites oficiais

    // Getters e Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getStoryline() {
        return storyline;
    }

    public void setStoryline(String storyline) {
        this.storyline = storyline;
    }

    public Long getFirstReleaseDate() {
        return firstReleaseDate;
    }

    public void setFirstReleaseDate(Long firstReleaseDate) {
        this.firstReleaseDate = firstReleaseDate;
    }

    public BigDecimal getAggregatedRating() {
        return aggregatedRating;
    }

    public void setAggregatedRating(BigDecimal aggregatedRating) {
        this.aggregatedRating = aggregatedRating;
    }

    public Integer getAggregatedRatingCount() {
        return aggregatedRatingCount;
    }

    public void setAggregatedRatingCount(Integer aggregatedRatingCount) {
        this.aggregatedRatingCount = aggregatedRatingCount;
    }

    public BigDecimal getRating() {
        return rating;
    }

    public void setRating(BigDecimal rating) {
        this.rating = rating;
    }

    public Integer getRatingCount() {
        return ratingCount;
    }

    public void setRatingCount(Integer ratingCount) {
        this.ratingCount = ratingCount;
    }

    public BigDecimal getTotalRating() {
        return totalRating;
    }

    public void setTotalRating(BigDecimal totalRating) {
        this.totalRating = totalRating;
    }

    public Integer getTotalRatingCount() {
        return totalRatingCount;
    }

    public void setTotalRatingCount(Integer totalRatingCount) {
        this.totalRatingCount = totalRatingCount;
    }

    public IgdbCoverDTO getCover() {
        return cover;
    }

    public void setCover(IgdbCoverDTO cover) {
        this.cover = cover;
    }

    public List<Long> getScreenshots() {
        return screenshots;
    }

    public void setScreenshots(List<Long> screenshots) {
        this.screenshots = screenshots;
    }

    public List<Long> getArtworks() {
        return artworks;
    }

    public void setArtworks(List<Long> artworks) {
        this.artworks = artworks;
    }

    public List<Long> getVideos() {
        return videos;
    }

    public void setVideos(List<Long> videos) {
        this.videos = videos;
    }

    public List<IgdbPlatformDTO> getPlatforms() {
        return platforms;
    }

    public void setPlatforms(List<IgdbPlatformDTO> platforms) {
        this.platforms = platforms;
    }

    public List<IgdbGenreDTO> getGenres() {
        return genres;
    }

    public void setGenres(List<IgdbGenreDTO> genres) {
        this.genres = genres;
    }

    public List<Long> getInvolvedCompanies() {
        return involvedCompanies;
    }

    public void setInvolvedCompanies(List<Long> involvedCompanies) {
        this.involvedCompanies = involvedCompanies;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public List<Long> getExternalGames() {
        return externalGames;
    }

    public void setExternalGames(List<Long> externalGames) {
        this.externalGames = externalGames;
    }

    public List<Long> getWebsites() {
        return websites;
    }

    public void setWebsites(List<Long> websites) {
        this.websites = websites;
    }
}
