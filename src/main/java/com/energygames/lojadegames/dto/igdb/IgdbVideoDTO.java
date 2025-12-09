package com.energygames.lojadegames.dto.igdb;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO para vídeos de jogos na IGDB
 * Ref: https://api-docs.igdb.com/#game-video
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class IgdbVideoDTO {

    private Long id;
    
    @JsonProperty("video_id")
    private String videoId; // ID do vídeo no YouTube
    
    private String name; // Título do vídeo

    // Getters e Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Retorna URL completa do vídeo no YouTube
     */
    public String getYoutubeUrl() {
        if (videoId == null) return null;
        return "https://www.youtube.com/watch?v=" + videoId;
    }
}
