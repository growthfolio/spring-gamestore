package com.energygames.lojadegames.service;

import org.springframework.stereotype.Service;

import com.energygames.lojadegames.dto.response.ImagemResponseDTO;
import com.energygames.lojadegames.dto.response.ImagemResponseDTO.ImagemUrlsDTO;
import com.energygames.lojadegames.dto.response.VideoResponseDTO;
import com.energygames.lojadegames.dto.response.VideoResponseDTO.VideoThumbsDTO;
import com.energygames.lojadegames.dto.response.VideoResponseDTO.VideoUrlsDTO;
import com.energygames.lojadegames.model.ProdutoImagem;
import com.energygames.lojadegames.model.ProdutoVideo;

/**
 * Serviço utilitário para geração de URLs de mídia
 * Gera URLs dinâmicas para imagens IGDB e vídeos YouTube
 */
@Service
public class MediaUrlService {

    private static final String IGDB_IMAGE_BASE_URL = "https://images.igdb.com/igdb/image/upload";
    private static final String YOUTUBE_EMBED_URL = "https://www.youtube.com/embed";
    private static final String YOUTUBE_WATCH_URL = "https://www.youtube.com/watch?v=";
    private static final String YOUTUBE_THUMB_URL = "https://img.youtube.com/vi";

    /**
     * Tamanhos de imagem disponíveis na IGDB
     * Documentação: https://api-docs.igdb.com/#images
     */
    public enum IgdbImageSize {
        // Thumbnails
        MICRO("micro", 35, 35),           // Para micro ícones
        THUMB("thumb", 90, 90),           // Para listas compactas
        
        // Covers (proporção 3:4)
        COVER_SMALL("cover_small", 90, 128),
        COVER_BIG("cover_big", 264, 374),
        
        // Screenshots (proporção 16:9)
        SCREENSHOT_MED("screenshot_med", 569, 320),
        SCREENSHOT_BIG("screenshot_big", 889, 500),
        SCREENSHOT_HUGE("screenshot_huge", 1280, 720),
        
        // Alta resolução
        HD_720P("720p", 1280, 720),
        HD_1080P("1080p", 1920, 1080),
        
        // Logos
        LOGO_MED("logo_med", 284, 160);

        private final String value;
        private final int width;
        private final int height;

        IgdbImageSize(String value, int width, int height) {
            this.value = value;
            this.width = width;
            this.height = height;
        }

        public String getValue() { return value; }
        public int getWidth() { return width; }
        public int getHeight() { return height; }
    }

    /**
     * Gera URL de imagem IGDB para um tamanho específico
     * @param imageId ID da imagem na IGDB (hash como "co1234")
     * @param size Tamanho desejado
     * @return URL completa da imagem
     */
    public String buildIgdbImageUrl(String imageId, IgdbImageSize size) {
        if (imageId == null || imageId.isBlank()) {
            return null;
        }
        return String.format("%s/t_%s/%s.jpg", IGDB_IMAGE_BASE_URL, size.getValue(), imageId);
    }

    /**
     * Gera URL de imagem IGDB com suporte a Retina (2x)
     * @param imageId ID da imagem na IGDB
     * @param size Tamanho desejado
     * @param retina Se true, adiciona sufixo _2x para alta densidade de pixels
     * @return URL completa da imagem
     */
    public String buildIgdbImageUrl(String imageId, IgdbImageSize size, boolean retina) {
        if (imageId == null || imageId.isBlank()) {
            return null;
        }
        String sizeValue = retina ? size.getValue() + "_2x" : size.getValue();
        return String.format("%s/t_%s/%s.jpg", IGDB_IMAGE_BASE_URL, sizeValue, imageId);
    }

    /**
     * Gera todas as URLs de imagem para um ProdutoImagem
     * @param imagem Entidade ProdutoImagem
     * @return DTO com todas as URLs ou null se não for imagem IGDB
     */
    public ImagemUrlsDTO buildAllImageUrls(ProdutoImagem imagem) {
        String imageId = imagem.getIdIgdb();
        
        // Se não tem imageId IGDB, retorna null (usar urlOriginal como fallback)
        if (imageId == null || imageId.isBlank()) {
            return null;
        }

        ImagemUrlsDTO urls = new ImagemUrlsDTO();
        
        // Thumbnails
        urls.setMicro(buildIgdbImageUrl(imageId, IgdbImageSize.MICRO));
        urls.setThumb(buildIgdbImageUrl(imageId, IgdbImageSize.THUMB));
        
        // Covers
        urls.setCoverSmall(buildIgdbImageUrl(imageId, IgdbImageSize.COVER_SMALL));
        urls.setCoverBig(buildIgdbImageUrl(imageId, IgdbImageSize.COVER_BIG));
        
        // Screenshots
        urls.setScreenshotMed(buildIgdbImageUrl(imageId, IgdbImageSize.SCREENSHOT_MED));
        urls.setScreenshotBig(buildIgdbImageUrl(imageId, IgdbImageSize.SCREENSHOT_BIG));
        urls.setScreenshotHuge(buildIgdbImageUrl(imageId, IgdbImageSize.SCREENSHOT_HUGE));
        
        // Alta resolução
        urls.setHd720(buildIgdbImageUrl(imageId, IgdbImageSize.HD_720P));
        urls.setHd1080(buildIgdbImageUrl(imageId, IgdbImageSize.HD_1080P));
        
        // Logo
        urls.setLogoMed(buildIgdbImageUrl(imageId, IgdbImageSize.LOGO_MED));

        return urls;
    }

    /**
     * Converte ProdutoImagem para ImagemResponseDTO com todas as URLs
     * @param imagem Entidade ProdutoImagem
     * @return DTO completo com metadados e URLs
     */
    public ImagemResponseDTO toImagemResponseDTO(ProdutoImagem imagem) {
        if (imagem == null) {
            return null;
        }

        ImagemResponseDTO dto = new ImagemResponseDTO();
        dto.setId(imagem.getId());
        dto.setTipo(imagem.getTipo());
        dto.setImagemPrincipal(imagem.getImagemPrincipal());
        dto.setOrdem(imagem.getOrdem());
        dto.setLarguraOriginal(imagem.getLargura());
        dto.setAlturaOriginal(imagem.getAltura());
        dto.setImageIdIgdb(imagem.getIdIgdb());
        dto.setUrlOriginal(imagem.getUrl());
        dto.setUrls(buildAllImageUrls(imagem));

        return dto;
    }

    // ==================== YOUTUBE VIDEO URLS ====================

    /**
     * Gera URL de embed do YouTube
     * @param videoId ID do vídeo no YouTube
     * @return URL de embed (para iframe)
     */
    public String buildYoutubeEmbedUrl(String videoId) {
        if (videoId == null || videoId.isBlank()) {
            return null;
        }
        return YOUTUBE_EMBED_URL + "/" + videoId;
    }

    /**
     * Gera URL de watch do YouTube
     * @param videoId ID do vídeo no YouTube
     * @return URL para assistir no YouTube
     */
    public String buildYoutubeWatchUrl(String videoId) {
        if (videoId == null || videoId.isBlank()) {
            return null;
        }
        return YOUTUBE_WATCH_URL + videoId;
    }

    /**
     * Gera URL de thumbnail do YouTube
     * @param videoId ID do vídeo no YouTube
     * @param size Tamanho: "default", "mqdefault", "hqdefault", "sddefault", "maxresdefault"
     * @return URL da thumbnail
     */
    public String buildYoutubeThumbnailUrl(String videoId, String size) {
        if (videoId == null || videoId.isBlank()) {
            return null;
        }
        return String.format("%s/%s/%s.jpg", YOUTUBE_THUMB_URL, videoId, size);
    }

    /**
     * Gera todas as URLs de vídeo para um ProdutoVideo
     * @param video Entidade ProdutoVideo
     * @return DTO com todas as URLs
     */
    public VideoUrlsDTO buildAllVideoUrls(ProdutoVideo video) {
        String videoId = video.getVideoId();
        
        if (videoId == null || videoId.isBlank()) {
            return null;
        }

        VideoUrlsDTO urls = new VideoUrlsDTO();
        urls.setEmbed(buildYoutubeEmbedUrl(videoId));
        urls.setWatch(buildYoutubeWatchUrl(videoId));
        
        // Thumbnails
        VideoThumbsDTO thumbs = new VideoThumbsDTO();
        thumbs.setDefaultThumb(buildYoutubeThumbnailUrl(videoId, "default"));
        thumbs.setMedium(buildYoutubeThumbnailUrl(videoId, "mqdefault"));
        thumbs.setHigh(buildYoutubeThumbnailUrl(videoId, "hqdefault"));
        thumbs.setStandard(buildYoutubeThumbnailUrl(videoId, "sddefault"));
        thumbs.setMaxres(buildYoutubeThumbnailUrl(videoId, "maxresdefault"));
        urls.setThumbnails(thumbs);

        return urls;
    }

    /**
     * Converte ProdutoVideo para VideoResponseDTO com todas as URLs
     * @param video Entidade ProdutoVideo
     * @return DTO completo com metadados e URLs
     */
    public VideoResponseDTO toVideoResponseDTO(ProdutoVideo video) {
        if (video == null) {
            return null;
        }

        VideoResponseDTO dto = new VideoResponseDTO();
        dto.setId(video.getId());
        dto.setTitulo(video.getTitulo());
        dto.setTipo(video.getTipo());
        dto.setOrdem(video.getOrdem());
        dto.setVideoId(video.getVideoId());
        dto.setUrls(buildAllVideoUrls(video));

        return dto;
    }
}
