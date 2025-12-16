package com.energygames.lojadegames.dto.response;

import com.energygames.lojadegames.enums.TipoImagemEnum;

/**
 * DTO estruturado para imagem de produto com URLs em múltiplos tamanhos
 * Permite ao frontend escolher o tamanho ideal para cada contexto
 */
public class ImagemResponseDTO {

    private Long id;
    private TipoImagemEnum tipo;
    private Boolean imagemPrincipal;
    private Integer ordem;
    private Integer larguraOriginal;
    private Integer alturaOriginal;
    
    /**
     * ID da imagem no IGDB (usado para gerar URLs dinâmicas)
     * Se null, usa urlOriginal como fallback
     */
    private String imageIdIgdb;
    
    /**
     * URL original armazenada no banco (fallback)
     */
    private String urlOriginal;
    
    /**
     * URLs em diferentes tamanhos para imagens IGDB
     */
    private ImagemUrlsDTO urls;

    // Inner class para URLs em múltiplos tamanhos
    public static class ImagemUrlsDTO {
        // Thumbnails pequenos
        private String micro;      // 35x35 - para micro ícones
        private String thumb;      // 90x90 - para listas compactas
        
        // Covers (proporção 3:4)
        private String coverSmall;  // 90x128
        private String coverBig;    // 264x374
        
        // Screenshots (proporção 16:9)
        private String screenshotMed;  // 569x320
        private String screenshotBig;  // 889x500
        private String screenshotHuge; // 1280x720
        
        // Alta resolução
        private String hd720;   // 1280x720
        private String hd1080;  // 1920x1080
        
        // Logos
        private String logoMed; // 284x160

        // Getters e Setters
        public String getMicro() {
            return micro;
        }

        public void setMicro(String micro) {
            this.micro = micro;
        }

        public String getThumb() {
            return thumb;
        }

        public void setThumb(String thumb) {
            this.thumb = thumb;
        }

        public String getCoverSmall() {
            return coverSmall;
        }

        public void setCoverSmall(String coverSmall) {
            this.coverSmall = coverSmall;
        }

        public String getCoverBig() {
            return coverBig;
        }

        public void setCoverBig(String coverBig) {
            this.coverBig = coverBig;
        }

        public String getScreenshotMed() {
            return screenshotMed;
        }

        public void setScreenshotMed(String screenshotMed) {
            this.screenshotMed = screenshotMed;
        }

        public String getScreenshotBig() {
            return screenshotBig;
        }

        public void setScreenshotBig(String screenshotBig) {
            this.screenshotBig = screenshotBig;
        }

        public String getScreenshotHuge() {
            return screenshotHuge;
        }

        public void setScreenshotHuge(String screenshotHuge) {
            this.screenshotHuge = screenshotHuge;
        }

        public String getHd720() {
            return hd720;
        }

        public void setHd720(String hd720) {
            this.hd720 = hd720;
        }

        public String getHd1080() {
            return hd1080;
        }

        public void setHd1080(String hd1080) {
            this.hd1080 = hd1080;
        }

        public String getLogoMed() {
            return logoMed;
        }

        public void setLogoMed(String logoMed) {
            this.logoMed = logoMed;
        }
    }

    // Getters e Setters do DTO principal
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TipoImagemEnum getTipo() {
        return tipo;
    }

    public void setTipo(TipoImagemEnum tipo) {
        this.tipo = tipo;
    }

    public Boolean getImagemPrincipal() {
        return imagemPrincipal;
    }

    public void setImagemPrincipal(Boolean imagemPrincipal) {
        this.imagemPrincipal = imagemPrincipal;
    }

    public Integer getOrdem() {
        return ordem;
    }

    public void setOrdem(Integer ordem) {
        this.ordem = ordem;
    }

    public Integer getLarguraOriginal() {
        return larguraOriginal;
    }

    public void setLarguraOriginal(Integer larguraOriginal) {
        this.larguraOriginal = larguraOriginal;
    }

    public Integer getAlturaOriginal() {
        return alturaOriginal;
    }

    public void setAlturaOriginal(Integer alturaOriginal) {
        this.alturaOriginal = alturaOriginal;
    }

    public String getImageIdIgdb() {
        return imageIdIgdb;
    }

    public void setImageIdIgdb(String imageIdIgdb) {
        this.imageIdIgdb = imageIdIgdb;
    }

    public String getUrlOriginal() {
        return urlOriginal;
    }

    public void setUrlOriginal(String urlOriginal) {
        this.urlOriginal = urlOriginal;
    }

    public ImagemUrlsDTO getUrls() {
        return urls;
    }

    public void setUrls(ImagemUrlsDTO urls) {
        this.urls = urls;
    }
}
