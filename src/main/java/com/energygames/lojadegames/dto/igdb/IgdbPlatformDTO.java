package com.energygames.lojadegames.dto.igdb;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO para plataformas de jogos na IGDB
 * Ref: https://api-docs.igdb.com/#platform
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class IgdbPlatformDTO {

    private Long id;
    private String name;
    private String slug;
    private String abbreviation; // Ex: "PS5", "XBSX", "NSW"
    
    @JsonProperty("alternative_name")
    private String alternativeName;
    
    private Integer category; // 1=console, 2=arcade, 3=platform, 4=operating_system, 5=portable_console, 6=computer
    
    private Integer generation; // Geração do console (8, 9, etc)
    
    @JsonProperty("platform_logo")
    private Long platformLogo; // ID do logo (precisa buscar em /platform_logos)

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

    public String getAbbreviation() {
        return abbreviation;
    }

    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public String getAlternativeName() {
        return alternativeName;
    }

    public void setAlternativeName(String alternativeName) {
        this.alternativeName = alternativeName;
    }

    public Integer getCategory() {
        return category;
    }

    public void setCategory(Integer category) {
        this.category = category;
    }

    public Integer getGeneration() {
        return generation;
    }

    public void setGeneration(Integer generation) {
        this.generation = generation;
    }

    public Long getPlatformLogo() {
        return platformLogo;
    }

    public void setPlatformLogo(Long platformLogo) {
        this.platformLogo = platformLogo;
    }
}
