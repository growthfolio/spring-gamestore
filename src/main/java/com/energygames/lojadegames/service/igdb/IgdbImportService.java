package com.energygames.lojadegames.service.igdb;

import com.energygames.lojadegames.dto.igdb.*;
import com.energygames.lojadegames.enums.OrigemEnum;
import com.energygames.lojadegames.model.Categoria;
import com.energygames.lojadegames.model.Produto;
import com.energygames.lojadegames.model.ProdutoOrigemExterna;
import com.energygames.lojadegames.repository.CategoriaRepository;
import com.energygames.lojadegames.repository.ProdutoOrigemExternaRepository;
import com.energygames.lojadegames.repository.ProdutoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Serviço de importação de jogos da IGDB
 * Orquestra a busca, mapeamento e persistência de dados
 */
@Service
public class IgdbImportService {

    private static final Logger log = LoggerFactory.getLogger(IgdbImportService.class);

    private final IgdbApiClient apiClient;
    private final IgdbMapperService mapper;
    private final ProdutoRepository produtoRepository;
    private final ProdutoOrigemExternaRepository origemExternaRepository;
    private final CategoriaRepository categoriaRepository;

    public IgdbImportService(
        IgdbApiClient apiClient,
        IgdbMapperService mapper,
        ProdutoRepository produtoRepository,
        ProdutoOrigemExternaRepository origemExternaRepository,
        CategoriaRepository categoriaRepository
    ) {
        this.apiClient = apiClient;
        this.mapper = mapper;
        this.produtoRepository = produtoRepository;
        this.origemExternaRepository = origemExternaRepository;
        this.categoriaRepository = categoriaRepository;
    }

    /**
     * Importa jogo por ID da IGDB
     * @param igdbGameId ID do jogo na IGDB
     * @return Produto importado e salvo
     * @throws IllegalArgumentException se jogo não encontrado
     */
    @Transactional
    public Produto importGameById(Long igdbGameId) {
        log.info("Iniciando importação de jogo IGDB ID: {}", igdbGameId);

        // Verifica se jogo já foi importado
        Optional<ProdutoOrigemExterna> existing = origemExternaRepository.findByOrigemAndIdExterno(
            OrigemEnum.IGDB, 
            igdbGameId.toString()
        );

        if (existing.isPresent()) {
            log.warn("Jogo IGDB ID {} já importado (Produto ID: {}). Use sincronização para atualizar.", 
                igdbGameId, existing.get().getProduto().getId());
            return existing.get().getProduto();
        }

        // Busca dados do jogo na IGDB
        IgdbGameDTO gameDTO = apiClient.getGameById(igdbGameId);
        if (gameDTO == null) {
            throw new IllegalArgumentException("Jogo IGDB ID " + igdbGameId + " não encontrado");
        }

        // Busca dados relacionados
        IgdbCoverDTO coverDTO = gameDTO.getCover();

        List<IgdbScreenshotDTO> screenshots = gameDTO.getScreenshots() != null 
            ? apiClient.getScreenshotsByIds(gameDTO.getScreenshots()) 
            : List.of();

        List<IgdbVideoDTO> videos = gameDTO.getVideos() != null 
            ? apiClient.getVideosByIds(gameDTO.getVideos()) 
            : List.of();

        List<IgdbPlatformDTO> platforms = gameDTO.getPlatforms() != null 
            ? gameDTO.getPlatforms()
            : List.of();

        List<IgdbGenreDTO> genres = gameDTO.getGenres() != null 
            ? gameDTO.getGenres()
            : List.of();

        // Mapeia para Produto
        Produto produto = mapper.mapGameToProduct(gameDTO, coverDTO, screenshots, videos, platforms, genres);

        // Persiste categorias novas primeiro (se necessário)
        List<Categoria> novasCategorias = produto.getGeneros().stream()
            .filter(c -> c.getId() == null)
            .toList();
            
        if (!novasCategorias.isEmpty()) {
            categoriaRepository.saveAll(novasCategorias);
            log.debug("{} novas categorias persistidas", novasCategorias.size());
        }

        // Persiste produto (cascade persiste imagens, vídeos e origem)
        Produto savedProduto = produtoRepository.save(produto);

        log.info("Jogo '{}' importado com sucesso (Produto ID: {})", savedProduto.getNome(), savedProduto.getId());
        return savedProduto;
    }

    /**
     * Busca jogos por nome na IGDB e retorna lista de candidatos para importação
     * @param gameName Nome do jogo para buscar
     * @param page Página atual (1-based)
     * @param limit Limite de resultados
     * @return Lista de DTOs de jogos encontrados
     */
    public List<IgdbGameDTO> searchGamesForImport(String gameName, int page, int limit) {
        int offset = Math.max(0, (page - 1) * limit);
        log.info("Buscando jogos na IGDB: '{}' (Pagina: {}, Offset: {})", gameName, page, offset);
        
        List<IgdbGameDTO> results = apiClient.searchGamesByName(gameName, limit, offset);
        
        log.info("Encontrados {} jogos na IGDB para '{}'", results.size(), gameName);
        return results;
    }

    /**
     * Importa jogos populares da IGDB (Método legado para compatibilidade ou uso interno)
     */
    @Transactional
    public List<Produto> importPopularGames(int quantity) {
        return importPopularGames(quantity, 0); // Offset 0
    }

    @Transactional
    public List<Produto> importPopularGames(int quantity, int offset) {
        log.info("Importando {} jogos populares da IGDB (offset: {})", quantity, offset);

        List<IgdbGameDTO> popularGames = apiClient.getPopularGames(quantity, offset);
        
        return popularGames.stream()
            .map(game -> {
                try {
                    // Verifica se já foi importado
                    Optional<ProdutoOrigemExterna> existing = origemExternaRepository.findByOrigemAndIdExterno(
                        OrigemEnum.IGDB, 
                        game.getId().toString()
                    );

                    if (existing.isPresent()) {
                        log.debug("Jogo '{}' já importado, pulando", game.getName());
                        return null; // Retorna null para filtrar duplicatas
                    }

                    return importGameById(game.getId());
                    
                } catch (Exception e) {
                    log.error("Erro ao importar jogo '{}' (IGDB ID: {}): {}", 
                        game.getName(), game.getId(), e.getMessage());
                    return null;
                }
            })
            .filter(produto -> produto != null)
            .toList();
    }

    /**
     * Busca jogos populares na IGDB para listagem (sem importar)
     * @param page Página atual (1-based)
     * @param limit Limite de resultados
     * @return Lista de DTOs de jogos populares
     */
    public List<IgdbGameDTO> getPopularGamesForImport(int page, int limit) {
        int offset = Math.max(0, (page - 1) * limit);
        log.info("Buscando jogos populares na IGDB (Pagina: {}, Offset: {})", page, offset);
        return apiClient.getPopularGames(limit, offset);
    }

    /**
     * Sincroniza produto existente com dados atualizados da IGDB
     * @param produtoId ID do produto no banco
     * @return Produto atualizado
     */
    @Transactional
    public Produto syncProduct(Long produtoId) {
        log.info("Sincronizando Produto ID {} com IGDB", produtoId);

        // Busca produto e origem
        Produto produto = produtoRepository.findById(produtoId)
            .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado: " + produtoId));

        ProdutoOrigemExterna origem = produto.getOrigemExterna();
        if (origem == null || origem.getOrigem() != OrigemEnum.IGDB) {
            throw new IllegalArgumentException("Produto não tem origem IGDB");
        }

        if (!origem.getSincronizacaoAtiva()) {
            log.warn("Sincronização desabilitada para Produto ID {}", produtoId);
            return produto;
        }

        // Busca dados atualizados na IGDB
        Long igdbId = Long.parseLong(origem.getIdExterno());
        IgdbGameDTO gameDTO = apiClient.getGameById(igdbId);
        
        if (gameDTO == null) {
            log.error("Jogo IGDB ID {} não encontrado. Produto pode ter sido removido da API.", igdbId);
            origem.setSincronizacaoAtiva(false);
            produtoRepository.save(produto);
            return produto;
        }

        // Busca dados relacionados
        IgdbCoverDTO coverDTO = gameDTO.getCover();

        List<IgdbScreenshotDTO> screenshots = gameDTO.getScreenshots() != null 
            ? apiClient.getScreenshotsByIds(gameDTO.getScreenshots()) 
            : List.of();

        List<IgdbVideoDTO> videos = gameDTO.getVideos() != null 
            ? apiClient.getVideosByIds(gameDTO.getVideos()) 
            : List.of();

        List<IgdbPlatformDTO> platforms = gameDTO.getPlatforms() != null 
            ? gameDTO.getPlatforms()
            : List.of();

        List<IgdbGenreDTO> genres = gameDTO.getGenres() != null 
            ? gameDTO.getGenres()
            : List.of();

        // Atualiza produto (preserva campos não gerenciados pela IGDB como preço)
        BigDecimal precoOriginal = produto.getPreco();

        Produto produtoAtualizado = mapper.mapGameToProduct(gameDTO, coverDTO, screenshots, videos, platforms, genres);
        
        // Preserva ID e campos customizados
        produtoAtualizado.setId(produto.getId());
        produtoAtualizado.setPreco(precoOriginal);

        // Atualiza timestamp de sincronização
        origem.registrarSincronizacao();

        // Salva
        Produto savedProduto = produtoRepository.save(produtoAtualizado);

        log.info("Produto '{}' sincronizado com sucesso", savedProduto.getNome());
        return savedProduto;
    }

    /**
     * Sincroniza todos produtos com sincronização ativa e desatualizados
     * @param diasDesatualizacao Número de dias desde última sincronização
     * @return Quantidade de produtos sincronizados
     */
    @Transactional
    public int syncOutdatedProducts(int diasDesatualizacao) {
        log.info("Sincronizando produtos desatualizados (> {} dias)", diasDesatualizacao);

        LocalDateTime dataLimite = LocalDateTime.now().minusDays(diasDesatualizacao);
        List<ProdutoOrigemExterna> desatualizados = origemExternaRepository
            .findProdutosParaSincronizar(OrigemEnum.IGDB, dataLimite);

        int count = 0;
        for (ProdutoOrigemExterna origem : desatualizados) {
            try {
                syncProduct(origem.getProduto().getId());
                count++;
            } catch (Exception e) {
                log.error("Erro ao sincronizar Produto ID {}: {}", 
                    origem.getProduto().getId(), e.getMessage());
            }
        }

        log.info("{} produtos sincronizados com sucesso", count);
        return count;
    }

    /**
     * Importa múltiplos jogos em lote por seus IDs da IGDB
     * @param igdbIds Lista de IDs dos jogos na IGDB
     * @return Lista de produtos importados (pode conter nulls para falhas)
     */
    @Transactional
    public List<Produto> importGamesBatch(List<Long> igdbIds) {
        log.info("Iniciando importação em lote de {} jogos", igdbIds.size());

        return igdbIds.stream()
            .map(igdbId -> {
                try {
                    // Verifica se já foi importado
                    Optional<ProdutoOrigemExterna> existing = origemExternaRepository.findByOrigemAndIdExterno(
                        OrigemEnum.IGDB, 
                        igdbId.toString()
                    );

                    if (existing.isPresent()) {
                        log.debug("Jogo IGDB ID {} já importado, retornando existente", igdbId);
                        return existing.get().getProduto();
                    }

                    return importGameById(igdbId);
                    
                } catch (Exception e) {
                    log.error("Erro ao importar jogo IGDB ID {}: {}", igdbId, e.getMessage());
                    return null; // Retorna null para falhas
                }
            })
            .toList();
    }

    /**
     * Busca detalhes completos de um jogo para preview
     * @param igdbId ID do jogo na IGDB
     * @return DTO do jogo ou null se não encontrado
     */
    public IgdbGameDTO getGameDetails(Long igdbId) {
        log.info("Buscando detalhes do jogo IGDB ID: {}", igdbId);
        return apiClient.getGameById(igdbId);
    }

    /**
     * Verifica status da integração IGDB
     * @return true se API está acessível
     */
    public boolean checkApiStatus() {
        return apiClient.isApiAvailable();
    }
}
