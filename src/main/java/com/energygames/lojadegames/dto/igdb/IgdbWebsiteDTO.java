package com.energygames.lojadegames.dto.igdb;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * DTO para websites oficiais de jogos na IGDB
 * Ref: https://api-docs.igdb.com/#website
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class IgdbWebsiteDTO {

    private Long id;
    private Integer category; // 1=oficial, 2=wikia, 3=wikipedia, 4=facebook, 5=twitter, etc
    private String url;
    private Boolean trusted;

    // Getters e Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getCategory() {
        return category;
    }

    public void setCategory(Integer category) {
        this.category = category;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Boolean getTrusted() {
        return trusted;
    }

    public void setTrusted(Boolean trusted) {
        this.trusted = trusted;
    }
}
