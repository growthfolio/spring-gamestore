package com.energygames.lojadegames.dto.igdb;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * DTO para links externos de jogos na IGDB
 * Ref: https://api-docs.igdb.com/#external-game
 * 
 * Suporta tanto objetos completos quanto IDs numéricos simples
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class IgdbExternalGameDTO {

    private Long id;
    private Integer category; // 1=Steam, 5=GOG, 13=Epic Games, 26=Amazon, etc
    private String uid; // ID externo na plataforma
    private String url;

    // Construtor padrão
    public IgdbExternalGameDTO() {}

    // Construtor para aceitar apenas ID numérico
    @JsonCreator
    public IgdbExternalGameDTO(Long id) {
        this.id = id;
    }

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

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "IgdbExternalGameDTO{" +
                "id=" + id +
                ", category=" + category +
                ", uid='" + uid + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
