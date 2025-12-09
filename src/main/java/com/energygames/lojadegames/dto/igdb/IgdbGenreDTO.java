package com.energygames.lojadegames.dto.igdb;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * DTO para gêneros de jogos na IGDB
 * Ref: https://api-docs.igdb.com/#genre
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class IgdbGenreDTO {

    private Long id;
    private String name;
    private String slug;

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
}
