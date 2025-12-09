package com.energygames.lojadegames.service.igdb;

import com.energygames.lojadegames.dto.igdb.*;
import com.energygames.lojadegames.enums.OrigemEnum;
import com.energygames.lojadegames.enums.StatusJogoEnum;
import com.energygames.lojadegames.enums.TipoImagemEnum;
import com.energygames.lojadegames.enums.TipoVideoEnum;
import com.energygames.lojadegames.model.*;
import com.energygames.lojadegames.repository.CategoriaRepository;
import com.energygames.lojadegames.repository.PlataformaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

/**
 * Serviço de mapeamento de dados IGDB para entidades do sistema
 * Converte DTOs da API IGDB para objetos JPA
 */
@Service
public class IgdbMapperService {

    private static final Logger log = LoggerFactory.getLogger(IgdbMapperService.class);

    private final PlataformaRepository plataformaRepository;
    private final CategoriaRepository categoriaRepository;

    public IgdbMapperService(PlataformaRepository plataformaRepository, CategoriaRepository categoriaRepository) {
        this.plataformaRepository = plataformaRepository;
        this.categoriaRepository = categoriaRepository;
    }

    /**
     * Mapeia IgdbGameDTO completo para Produto (sem persistir relacionamentos)
     * @param gameDTO Dados do jogo da IGDB
     * @param coverDTO Capa do jogo (opcional)
     * @param screenshots Screenshots do jogo (opcional)
     * @param videos Vídeos do jogo (opcional)
     * @param platforms Plataformas do jogo (opcional)
     * @param genres Gêneros do jogo (opcional)
     * @return Produto mapeado (ainda não persistido)
     */
    public Produto mapGameToProduct(
        IgdbGameDTO gameDTO,
        IgdbCoverDTO coverDTO,
        List<IgdbScreenshotDTO> screenshots,
        List<IgdbVideoDTO> videos,
        List<IgdbPlatformDTO> platforms,
        List<IgdbGenreDTO> genres
    ) {
        log.debug("Mapeando jogo IGDB ID {} para Produto", gameDTO.getId());

        Produto produto = new Produto();
        
        // Campos básicos
        produto.setNome(gameDTO.getName());
        produto.setSlug(gameDTO.getSlug());
        produto.setDescricao(truncate(gameDTO.getSummary(), 500)); // Limite do banco
        produto.setDescricaoCompleta(gameDTO.getStoryline() != null ? gameDTO.getStoryline() : gameDTO.getSummary());
        
        // Data de lançamento
        if (gameDTO.getFirstReleaseDate() != null) {
            LocalDate dataLancamento = Instant.ofEpochSecond(gameDTO.getFirstReleaseDate())
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
            produto.setDataLancamento(dataLancamento);
        }

        // Ratings (IGDB usa escala 0-100, convertemos para 0-10)
        if (gameDTO.getTotalRating() != null) {
            produto.setRatingIgdb(gameDTO.getTotalRating().divide(BigDecimal.TEN, 2, RoundingMode.HALF_UP));
        }
        produto.setTotalVotosExternos(gameDTO.getTotalRatingCount() != null ? gameDTO.getTotalRatingCount() : 0);
        
        // Status do jogo
        produto.setStatus(mapIgdbStatusToEnum(gameDTO.getStatus()));
        
        // Preço padrão (IGDB não fornece preços, definir manualmente depois)
        produto.setPreco(BigDecimal.ZERO);
        
        // Produto inicialmente ativo apenas se lançado
        produto.setAtivo(produto.getStatus() == StatusJogoEnum.RELEASED);

        // Origem externa
        ProdutoOrigemExterna origem = new ProdutoOrigemExterna();
        origem.setProduto(produto);
        origem.setOrigem(OrigemEnum.IGDB);
        origem.setIdExterno(gameDTO.getId().toString());
        origem.setUrlExterna(String.format("https://www.igdb.com/games/%s", gameDTO.getSlug()));
        origem.setSincronizacaoAtiva(true);
        produto.setOrigemExterna(origem);

        // Imagens estruturadas
        mapImages(produto, coverDTO, screenshots);

        // Vídeos
        mapVideos(produto, videos);

        // Links externos
        mapExternalLinks(produto, gameDTO);

        // Plataformas (requer busca no banco)
        mapPlatforms(produto, platforms);

        // Gêneros (requer busca no banco)
        mapGenres(produto, genres);

        log.info("Produto '{}' mapeado com sucesso a partir da IGDB", produto.getNome());
        return produto;
    }

    /**
     * Mapeia imagens (capa + screenshots) para ProdutoImagem
     */
    private void mapImages(Produto produto, IgdbCoverDTO coverDTO, List<IgdbScreenshotDTO> screenshots) {
        Set<ProdutoImagem> imagens = new HashSet<>();
        int ordem = 1;

        // Capa como imagem principal
        if (coverDTO != null && coverDTO.getImageId() != null) {
            ProdutoImagem capa = new ProdutoImagem();
            capa.setProduto(produto);
            capa.setUrl(coverDTO.buildImageUrl("cover_big")); // 264x374
            capa.setTipo(TipoImagemEnum.CAPA);
            capa.setImagemPrincipal(true);
            capa.setOrdem(ordem++);
            capa.setLargura(coverDTO.getWidth());
            capa.setAltura(coverDTO.getHeight());
            capa.setIdIgdb(coverDTO.getId().toString());
            imagens.add(capa);

            log.debug("Capa adicionada: {}", capa.getUrl());
        }

        // Screenshots
        if (screenshots != null) {
            for (IgdbScreenshotDTO screenshot : screenshots) {
                ProdutoImagem img = new ProdutoImagem();
                img.setProduto(produto);
                img.setUrl(screenshot.buildImageUrl("screenshot_big")); // 889x500
                img.setTipo(TipoImagemEnum.SCREENSHOT);
                img.setImagemPrincipal(false);
                img.setOrdem(ordem++);
                img.setLargura(screenshot.getWidth());
                img.setAltura(screenshot.getHeight());
                img.setIdIgdb(screenshot.getId().toString());
                imagens.add(img);
            }
            log.debug("{} screenshots adicionados", screenshots.size());
        }

        produto.setImagensEstruturadas(imagens);
    }

    /**
     * Mapeia vídeos para ProdutoVideo
     */
    private void mapVideos(Produto produto, List<IgdbVideoDTO> videos) {
        if (videos == null || videos.isEmpty()) return;

        Set<ProdutoVideo> produtoVideos = new HashSet<>();
        int ordem = 1;

        for (IgdbVideoDTO video : videos) {
            ProdutoVideo pv = new ProdutoVideo();
            pv.setProduto(produto);
            pv.setVideoId(video.getVideoId());
            pv.setTitulo(video.getName());
            pv.setTipo(TipoVideoEnum.TRAILER); // IGDB não categoriza, assumir trailer
            pv.setOrdem(ordem++);
            produtoVideos.add(pv);
        }

        produto.setVideos(produtoVideos);
        log.debug("{} vídeos adicionados", videos.size());
    }

    /**
     * Mapeia links externos (Steam, Epic, etc)
     */
    private void mapExternalLinks(Produto produto, IgdbGameDTO gameDTO) {
        Map<String, String> links = new HashMap<>();

        // External games (Steam, GOG, Epic, etc)
        if (gameDTO.getExternalGames() != null) {
            for (IgdbExternalGameDTO external : gameDTO.getExternalGames()) {
                String platform = mapExternalGameCategory(external.getCategory());
                if (platform != null && external.getUrl() != null) {
                    links.put(platform, external.getUrl());
                }
            }
        }

        // Websites oficiais
        if (gameDTO.getWebsites() != null) {
            for (IgdbWebsiteDTO website : gameDTO.getWebsites()) {
                String category = mapWebsiteCategory(website.getCategory());
                if (category != null && website.getUrl() != null) {
                    links.put(category, website.getUrl());
                }
            }
        }

        produto.setLinksExternos(links);
        log.debug("{} links externos adicionados", links.size());
    }

    /**
     * Mapeia plataformas (busca no banco por idIgdb)
     */
    private void mapPlatforms(Produto produto, List<IgdbPlatformDTO> platforms) {
        if (platforms == null || platforms.isEmpty()) return;

        Set<Plataforma> plataformas = new HashSet<>();

        for (IgdbPlatformDTO platformDTO : platforms) {
            // Converter Long para Integer pois Plataforma.idIgdb é Integer
            Integer idIgdbInt = platformDTO.getId() != null ? platformDTO.getId().intValue() : null;
            Optional<Plataforma> existing = plataformaRepository.findByIdIgdb(idIgdbInt);
            
            if (existing.isPresent()) {
                plataformas.add(existing.get());
            } else {
                log.warn("Plataforma IGDB ID {} ('{}') não encontrada no banco. Execute seed migration primeiro.", 
                    platformDTO.getId(), platformDTO.getName());
            }
        }

        produto.setPlataformas(plataformas);
        log.debug("{} plataformas vinculadas", plataformas.size());
    }

    /**
     * Mapeia gêneros (busca no banco por idIgdb ou cria novo)
     */
    private void mapGenres(Produto produto, List<IgdbGenreDTO> genres) {
        if (genres == null || genres.isEmpty()) return;

        Set<Categoria> categorias = new HashSet<>();

        for (IgdbGenreDTO genreDTO : genres) {
            // Converter Long para Integer pois Categoria.idIgdb é Integer
            Integer idIgdbInt = genreDTO.getId() != null ? genreDTO.getId().intValue() : null;
            Optional<Categoria> existing = categoriaRepository.findByIdIgdb(idIgdbInt);
            
            Categoria categoria;
            if (existing.isPresent()) {
                categoria = existing.get();
            } else {
                // Criar nova categoria a partir do gênero IGDB
                categoria = new Categoria();
                categoria.setTipo(genreDTO.getName()); // Categoria usa 'tipo' não 'nome'
                categoria.setSlug(genreDTO.getSlug());
                categoria.setIdIgdb(idIgdbInt);
                categoria.setDescricao("Gênero importado da IGDB");
                categoria.setAtivo(true);
                // Será persistida pelo CascadeType.ALL ou manualmente
                log.info("Nova categoria '{}' criada a partir de gênero IGDB", genreDTO.getName());
            }
            
            categorias.add(categoria);
        }

        produto.setGeneros(categorias);
        log.debug("{} gêneros vinculados", categorias.size());
    }

    /**
     * Converte status IGDB para StatusJogoEnum
     * IGDB: 0=Released, 2=Alpha, 3=Beta, 4=Early Access, 5=Offline, 6=Cancelled, 7=Rumored, 8=Delisted
     */
    private StatusJogoEnum mapIgdbStatusToEnum(Integer status) {
        if (status == null) return StatusJogoEnum.RELEASED;
        
        return switch (status) {
            case 0 -> StatusJogoEnum.RELEASED;
            case 2 -> StatusJogoEnum.ALPHA;
            case 3 -> StatusJogoEnum.BETA;
            case 4 -> StatusJogoEnum.EARLY_ACCESS;
            case 6 -> StatusJogoEnum.CANCELLED;
            case 7, 8 -> StatusJogoEnum.DISCONTINUED;
            default -> StatusJogoEnum.UPCOMING;
        };
    }

    /**
     * Mapeia categoria de external_game para nome de plataforma
     */
    private String mapExternalGameCategory(Integer category) {
        if (category == null) return null;
        
        return switch (category) {
            case 1 -> "Steam";
            case 5 -> "GOG";
            case 13 -> "Epic Games";
            case 26 -> "Amazon";
            default -> "Loja-" + category;
        };
    }

    /**
     * Mapeia categoria de website para nome
     */
    private String mapWebsiteCategory(Integer category) {
        if (category == null) return null;
        
        return switch (category) {
            case 1 -> "Site Oficial";
            case 2 -> "Wikia";
            case 3 -> "Wikipedia";
            case 4 -> "Facebook";
            case 5 -> "Twitter";
            case 6 -> "Twitch";
            case 8 -> "Instagram";
            case 9 -> "YouTube";
            case 13 -> "Steam";
            case 14 -> "Reddit";
            case 16 -> "Discord";
            default -> "Link-" + category;
        };
    }

    /**
     * Trunca string para tamanho máximo
     */
    private String truncate(String text, int maxLength) {
        if (text == null) return null;
        return text.length() > maxLength ? text.substring(0, maxLength - 3) + "..." : text;
    }
}
