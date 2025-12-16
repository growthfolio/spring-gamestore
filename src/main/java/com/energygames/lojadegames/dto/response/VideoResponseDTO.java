package com.energygames.lojadegames.dto.response;

import com.energygames.lojadegames.enums.TipoVideoEnum;

/**
 * DTO estruturado para vídeo de produto com URLs para YouTube e thumbnails
 */
public class VideoResponseDTO {

    private Long id;
    private String titulo;
    private TipoVideoEnum tipo;
    private Integer ordem;
    
    /**
     * ID do vídeo no YouTube
     */
    private String videoId;
    
    /**
     * URLs do vídeo e thumbnails
     */
    private VideoUrlsDTO urls;

    // Inner class para URLs de vídeo
    public static class VideoUrlsDTO {
        /**
         * URL para embed do YouTube (iframe)
         * Ex: https://www.youtube.com/embed/abc123
         */
        private String embed;
        
        /**
         * URL para abrir no YouTube
         * Ex: https://www.youtube.com/watch?v=abc123
         */
        private String watch;
        
        /**
         * Thumbnails do YouTube em diferentes tamanhos
         */
        private VideoThumbsDTO thumbnails;

        public String getEmbed() {
            return embed;
        }

        public void setEmbed(String embed) {
            this.embed = embed;
        }

        public String getWatch() {
            return watch;
        }

        public void setWatch(String watch) {
            this.watch = watch;
        }

        public VideoThumbsDTO getThumbnails() {
            return thumbnails;
        }

        public void setThumbnails(VideoThumbsDTO thumbnails) {
            this.thumbnails = thumbnails;
        }
    }

    // Inner class para thumbnails de vídeo
    public static class VideoThumbsDTO {
        private String defaultThumb;  // 120x90
        private String medium;        // 320x180
        private String high;          // 480x360
        private String standard;      // 640x480
        private String maxres;        // 1280x720

        public String getDefaultThumb() {
            return defaultThumb;
        }

        public void setDefaultThumb(String defaultThumb) {
            this.defaultThumb = defaultThumb;
        }

        public String getMedium() {
            return medium;
        }

        public void setMedium(String medium) {
            this.medium = medium;
        }

        public String getHigh() {
            return high;
        }

        public void setHigh(String high) {
            this.high = high;
        }

        public String getStandard() {
            return standard;
        }

        public void setStandard(String standard) {
            this.standard = standard;
        }

        public String getMaxres() {
            return maxres;
        }

        public void setMaxres(String maxres) {
            this.maxres = maxres;
        }
    }

    // Getters e Setters do DTO principal
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public TipoVideoEnum getTipo() {
        return tipo;
    }

    public void setTipo(TipoVideoEnum tipo) {
        this.tipo = tipo;
    }

    public Integer getOrdem() {
        return ordem;
    }

    public void setOrdem(Integer ordem) {
        this.ordem = ordem;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public VideoUrlsDTO getUrls() {
        return urls;
    }

    public void setUrls(VideoUrlsDTO urls) {
        this.urls = urls;
    }
}
