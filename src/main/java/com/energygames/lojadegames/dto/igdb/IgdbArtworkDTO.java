package com.energygames.lojadegames.dto.igdb;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO para artworks de jogos na IGDB
 * Artworks são imagens promocionais oficiais (diferente de screenshots)
 * Ref: https://api-docs.igdb.com/#artwork
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class IgdbArtworkDTO {

    private Long id;
    
    @JsonProperty("image_id")
    private String imageId; // ID da imagem (usado para construir URLs)
    
    private String url; // URL completa (opcional)
    
    private Integer width;
    private Integer height;
    
    @JsonProperty("alpha_channel")
    private Boolean alphaChannel;
    
    private Boolean animated;

    // Getters e Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Boolean getAlphaChannel() {
        return alphaChannel;
    }

    public void setAlphaChannel(Boolean alphaChannel) {
        this.alphaChannel = alphaChannel;
    }

    public Boolean getAnimated() {
        return animated;
    }

    public void setAnimated(Boolean animated) {
        this.animated = animated;
    }

    /**
     * Constrói URL da imagem IGDB no tamanho especificado
     * Tamanhos recomendados para artworks: screenshot_med, screenshot_big, screenshot_huge, 720p, 1080p
     */
    public String buildImageUrl(String size) {
        if (imageId == null) return null;
        return String.format("https://images.igdb.com/igdb/image/upload/t_%s/%s.jpg", size, imageId);
    }
}
