package com.energygames.lojadegames.dto.igdb;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO para screenshots de jogos na IGDB
 * Ref: https://api-docs.igdb.com/#screenshot
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class IgdbScreenshotDTO {

    private Long id;
    
    @JsonProperty("image_id")
    private String imageId; // ID da imagem (usado para construir URLs)
    
    private String url; // URL completa (opcional)
    
    private Integer width;
    private Integer height;

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

    /**
     * Constrói URL da imagem IGDB no tamanho especificado
     * Tamanhos: screenshot_med (569x320), screenshot_big (889x500), screenshot_huge (1280x720), 1080p (1920x1080)
     */
    public String buildImageUrl(String size) {
        if (imageId == null) return null;
        return String.format("https://images.igdb.com/igdb/image/upload/t_%s/%s.jpg", size, imageId);
    }
}
