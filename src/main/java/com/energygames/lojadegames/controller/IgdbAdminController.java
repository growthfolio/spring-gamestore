package com.energygames.lojadegames.controller;

import com.energygames.lojadegames.configuration.IgdbConfigProperties;
import com.energygames.lojadegames.dto.igdb.IgdbGameDTO;
import com.energygames.lojadegames.dto.response.IgdbImportStatusDTO;
import com.energygames.lojadegames.dto.response.IgdbSearchResultDTO;
import com.energygames.lojadegames.dto.response.IgdbSyncStatsDTO;
import com.energygames.lojadegames.enums.OrigemEnum;
import com.energygames.lojadegames.model.Produto;
import com.energygames.lojadegames.model.ProdutoOrigemExterna;
import com.energygames.lojadegames.repository.ProdutoOrigemExternaRepository;
import com.energygames.lojadegames.repository.ProdutoRepository;
import com.energygames.lojadegames.service.igdb.IgdbApiClient;
import com.energygames.lojadegames.service.igdb.IgdbImportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Controller administrativo para gerenciar importação de jogos da IGDB
 * Requer role ADMIN para acesso
 */
@RestController
@RequestMapping("/admin/igdb")
@Tag(name = "IGDB Admin", description = "Endpoints administrativos para integração IGDB")
@SecurityRequirement(name = "bearer-key")
@PreAuthorize("hasRole('ADMIN')")
public class IgdbAdminController {

    private static final Logger log = LoggerFactory.getLogger(IgdbAdminController.class);

    private final IgdbImportService importService;
    private final IgdbApiClient apiClient;
    private final IgdbConfigProperties config;
    private final ProdutoRepository produtoRepository;
    private final ProdutoOrigemExternaRepository origemExternaRepository;
    private final com.energygames.lojadegames.scheduler.IgdbSyncScheduler syncScheduler;

    public IgdbAdminController(
        IgdbImportService importService,
        IgdbApiClient apiClient,
        IgdbConfigProperties config,
        ProdutoRepository produtoRepository,
        ProdutoOrigemExternaRepository origemExternaRepository,
        com.energygames.lojadegames.scheduler.IgdbSyncScheduler syncScheduler
    ) {
        this.importService = importService;
        this.apiClient = apiClient;
        this.config = config;
        this.produtoRepository = produtoRepository;
        this.origemExternaRepository = origemExternaRepository;
        this.syncScheduler = syncScheduler;
    }

    @Operation(summary = "Importar jogo por ID da IGDB", description = "Importa um jogo específico da IGDB usando seu ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Jogo importado com sucesso"),
        @ApiResponse(responseCode = "400", description = "ID inválido ou jogo não encontrado"),
        @ApiResponse(responseCode = "500", description = "Erro na importação")
    })
    @PostMapping("/import/{igdbId}")
    public ResponseEntity<IgdbImportStatusDTO> importGameById(@PathVariable Long igdbId) {
        log.info("Admin requisitou importação de jogo IGDB ID: {}", igdbId);

        try {
            Produto produto = importService.importGameById(igdbId);
            return ResponseEntity.ok(
                IgdbImportStatusDTO.sucesso(produto.getId(), produto.getNome(), igdbId)
            );
        } catch (IllegalArgumentException e) {
            log.warn("Jogo IGDB ID {} não encontrado: {}", igdbId, e.getMessage());
            return ResponseEntity.badRequest()
                .body(IgdbImportStatusDTO.erro(e.getMessage()));
        } catch (Exception e) {
            log.error("Erro ao importar jogo IGDB ID {}", igdbId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(IgdbImportStatusDTO.erro("Erro interno: " + e.getMessage()));
        }
    }

    @Operation(summary = "Buscar jogos na IGDB", description = "Busca jogos por nome na IGDB e retorna candidatos para importação")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Busca realizada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Parâmetros inválidos")
    })
    @GetMapping("/search")
    public ResponseEntity<List<IgdbSearchResultDTO>> searchGames(
        @RequestParam(required = false) String nome,
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(defaultValue = "20") int limit
    ) {
        log.info("Admin buscando jogos na IGDB: '{}' (Pagina: {}, Limit: {})", nome, page, limit);

        try {
            List<IgdbGameDTO> games;
            
            if (nome != null && !nome.trim().isEmpty()) {
                games = importService.searchGamesForImport(nome, page, limit);
            } else {
                games = importService.getPopularGamesForImport(page, limit);
            }
            
            List<IgdbSearchResultDTO> results = games.stream()
                .map(game -> {
                    IgdbSearchResultDTO dto = new IgdbSearchResultDTO();
                    dto.setIgdbId(game.getId());
                    dto.setNome(game.getName());
                    dto.setSlug(game.getSlug());
                    dto.setDescricao(game.getSummary());
                    
                    // Data de lançamento
                    if (game.getFirstReleaseDate() != null) {
                        dto.setDataLancamento(
                            Instant.ofEpochSecond(game.getFirstReleaseDate())
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
                                .toString()
                        );
                    }
                    
                    // Rating (manter 0-100)
                    if (game.getTotalRating() != null) {
                        dto.setRating(game.getTotalRating().doubleValue());
                    }

                    // Capa
                    if (game.getCover() != null && game.getCover().getUrl() != null) {
                        String url = game.getCover().getUrl();
                        if (url.startsWith("//")) {
                            url = "https:" + url;
                        }
                        dto.setUrlCapa(url.replace("t_thumb", "t_cover_big"));
                    }

                    // Plataformas
                    if (game.getPlatforms() != null) {
                        dto.setPlataformas(game.getPlatforms().stream()
                            .map(p -> p.getName())
                            .collect(Collectors.toList()));
                    }

                    // Gêneros
                    if (game.getGenres() != null) {
                        dto.setGeneros(game.getGenres().stream()
                            .map(g -> g.getName())
                            .collect(Collectors.toList()));
                    }
                    
                    // Verificar se já foi importado
                    Optional<ProdutoOrigemExterna> existing = origemExternaRepository
                        .findByOrigemAndIdExterno(OrigemEnum.IGDB, game.getId().toString());
                    
                    if (existing.isPresent()) {
                        dto.setJaImportado(true);
                        dto.setProdutoIdLocal(existing.get().getProduto().getId());
                    } else {
                        dto.setJaImportado(false);
                    }
                    
                    return dto;
                })
                .collect(Collectors.toList());

            return ResponseEntity.ok(results);

        } catch (Exception e) {
            log.error("Erro ao buscar jogos na IGDB: {}", e.getMessage(), e);
            // Retorna o erro no corpo para facilitar o debug
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .header("X-Error-Message", e.getMessage())
                .body(List.of()); // Retorna lista vazia ou poderia retornar um DTO de erro se a assinatura permitisse
        }
    }

    @Operation(summary = "Importar jogos populares", description = "Importa jogos populares da IGDB em lote")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Importação concluída"),
        @ApiResponse(responseCode = "400", description = "Quantidade inválida")
    })
    @PostMapping("/import/popular")
    public ResponseEntity<List<IgdbImportStatusDTO>> importPopularGames(
        @RequestParam(defaultValue = "10") int quantidade
    ) {
        log.info("Admin requisitou importação de {} jogos populares", quantidade);

        if (quantidade < 1 || quantidade > 100) {
            return ResponseEntity.badRequest().build();
        }

        try {
            List<Produto> produtos = importService.importPopularGames(quantidade);
            
            List<IgdbImportStatusDTO> results = produtos.stream()
                .map(p -> IgdbImportStatusDTO.sucesso(
                    p.getId(), 
                    p.getNome(), 
                    Long.parseLong(p.getOrigemExterna().getIdExterno())
                ))
                .collect(Collectors.toList());

            return ResponseEntity.ok(results);

        } catch (Exception e) {
            log.error("Erro ao importar jogos populares", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "Sincronizar produto específico", description = "Atualiza dados de um produto com informações mais recentes da IGDB")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Produto sincronizado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Produto não encontrado"),
        @ApiResponse(responseCode = "400", description = "Produto não tem origem IGDB")
    })
    @PutMapping("/sync/{produtoId}")
    public ResponseEntity<IgdbImportStatusDTO> syncProduct(@PathVariable Long produtoId) {
        log.info("Admin requisitou sincronização do Produto ID: {}", produtoId);

        try {
            Produto produto = importService.syncProduct(produtoId);
            return ResponseEntity.ok(
                IgdbImportStatusDTO.sucesso(
                    produto.getId(), 
                    produto.getNome(), 
                    Long.parseLong(produto.getOrigemExterna().getIdExterno())
                )
            );
        } catch (IllegalArgumentException e) {
            log.warn("Erro ao sincronizar produto {}: {}", produtoId, e.getMessage());
            return ResponseEntity.badRequest()
                .body(IgdbImportStatusDTO.erro(e.getMessage()));
        } catch (Exception e) {
            log.error("Erro ao sincronizar produto {}", produtoId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(IgdbImportStatusDTO.erro("Erro interno: " + e.getMessage()));
        }
    }

    @Operation(summary = "Sincronizar todos produtos desatualizados", description = "Sincroniza produtos IGDB que não foram atualizados no intervalo configurado")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Sincronização concluída")
    })
    @PostMapping("/sync/all")
    public ResponseEntity<IgdbSyncStatsDTO> syncAllProducts() {
        log.info("Admin requisitou sincronização de todos produtos desatualizados");

        try {
            int diasDesatualizacao = config.getSyncIntervalDays();
            int count = importService.syncOutdatedProducts(diasDesatualizacao);

            IgdbSyncStatsDTO stats = new IgdbSyncStatsDTO();
            stats.setProdutosSincronizados(count);
            stats.setUltimaSincronizacao(LocalDateTime.now());
            stats.setApiDisponivel(true);
            stats.setStatusApi("OK");

            return ResponseEntity.ok(stats);

        } catch (Exception e) {
            log.error("Erro ao sincronizar produtos", e);
            
            IgdbSyncStatsDTO stats = new IgdbSyncStatsDTO();
            stats.setProdutosSincronizados(0);
            stats.setErros(1);
            stats.setApiDisponivel(false);
            stats.setStatusApi("ERRO: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(stats);
        }
    }

    @Operation(summary = "Ativar/desativar sincronização de produto", description = "Liga ou desliga a sincronização automática de um produto")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Status de sincronização atualizado"),
        @ApiResponse(responseCode = "404", description = "Produto não encontrado")
    })
    @PutMapping("/sync/toggle/{produtoId}")
    public ResponseEntity<IgdbImportStatusDTO> toggleSync(
        @PathVariable Long produtoId,
        @RequestParam boolean ativo
    ) {
        log.info("Admin {} sincronização do Produto ID: {}", 
            ativo ? "ativou" : "desativou", produtoId);

        try {
            Optional<Produto> produtoOpt = produtoRepository.findById(produtoId);
            if (produtoOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Produto produto = produtoOpt.get();
            ProdutoOrigemExterna origem = produto.getOrigemExterna();
            
            if (origem == null || origem.getOrigem() != OrigemEnum.IGDB) {
                return ResponseEntity.badRequest()
                    .body(IgdbImportStatusDTO.erro("Produto não tem origem IGDB"));
            }

            origem.setSincronizacaoAtiva(ativo);
            produtoRepository.save(produto);

            IgdbImportStatusDTO response = new IgdbImportStatusDTO();
            response.setSucesso(true);
            response.setMensagem("Sincronização " + (ativo ? "ativada" : "desativada"));
            response.setProdutoId(produtoId);
            response.setNomeProduto(produto.getNome());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Erro ao alterar status de sincronização", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(IgdbImportStatusDTO.erro(e.getMessage()));
        }
    }

    @Operation(summary = "Obter estatísticas IGDB", description = "Retorna estatísticas sobre produtos importados da IGDB")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Estatísticas obtidas com sucesso")
    })
    @GetMapping("/stats")
    public ResponseEntity<IgdbSyncStatsDTO> getStats() {
        log.info("Admin requisitou estatísticas IGDB");

        try {
            IgdbSyncStatsDTO stats = new IgdbSyncStatsDTO();
            
            // Contadores
            stats.setTotalProdutos(Long.valueOf(produtoRepository.count()).intValue());
            stats.setProdutosIgdb(origemExternaRepository.countByOrigem(OrigemEnum.IGDB).intValue());
            stats.setProdutosAtivos(origemExternaRepository.countByOrigemAndSincronizacaoAtivaTrue(OrigemEnum.IGDB).intValue());
            
            // Produtos desatualizados
            LocalDateTime dataLimite = LocalDateTime.now().minusDays(config.getSyncIntervalDays());
            List<ProdutoOrigemExterna> desatualizados = origemExternaRepository
                .findProdutosParaSincronizar(OrigemEnum.IGDB, dataLimite);
            stats.setProdutosDesatualizados(desatualizados.size());
            
            // Última sincronização (pegar o mais recente)
            Optional<ProdutoOrigemExterna> maisRecente = origemExternaRepository
                .findAll()
                .stream()
                .filter(o -> o.getOrigem() == OrigemEnum.IGDB)
                .filter(o -> o.getDataUltimaSincronizacao() != null)
                .max((o1, o2) -> o1.getDataUltimaSincronizacao().compareTo(o2.getDataUltimaSincronizacao()));
            
            if (maisRecente.isPresent()) {
                stats.setUltimaSincronizacao(maisRecente.get().getDataUltimaSincronizacao());
            }
            
            // Status da API
            boolean apiOk = importService.checkApiStatus();
            stats.setApiDisponivel(apiOk);
            stats.setStatusApi(apiOk ? "Operacional" : "Indisponível");

            return ResponseEntity.ok(stats);

        } catch (Exception e) {
            log.error("Erro ao obter estatísticas", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "Verificar status da API IGDB", description = "Testa conectividade com a API da IGDB")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "API acessível"),
        @ApiResponse(responseCode = "503", description = "API indisponível")
    })
    @GetMapping("/status")
    public ResponseEntity<IgdbSyncStatsDTO> checkApiStatus() {
        log.info("Admin verificou status da API IGDB");

        IgdbSyncStatsDTO stats = new IgdbSyncStatsDTO();
        
        boolean available = importService.checkApiStatus();
        stats.setApiDisponivel(available);
        stats.setStatusApi(available ? "Operacional" : "Indisponível");

        return available 
            ? ResponseEntity.ok(stats)
            : ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(stats);
    }

    @Operation(summary = "Executar sincronização manual", description = "Força execução imediata da sincronização de produtos desatualizados")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Sincronização executada com sucesso"),
        @ApiResponse(responseCode = "500", description = "Erro na sincronização")
    })
    @PostMapping("/sync/manual")
    public ResponseEntity<IgdbSyncStatsDTO> executeSyncManually() {
        log.info("Admin requisitou execução manual de sincronização");

        try {
            int produtosSincronizados = syncScheduler.executarSincronizacaoManual();

            IgdbSyncStatsDTO stats = new IgdbSyncStatsDTO();
            stats.setProdutosSincronizados(produtosSincronizados);
            stats.setUltimaSincronizacao(LocalDateTime.now());
            stats.setApiDisponivel(true);
            stats.setStatusApi("Sincronização manual concluída");

            return ResponseEntity.ok(stats);

        } catch (Exception e) {
            log.error("Erro na sincronização manual", e);

            IgdbSyncStatsDTO stats = new IgdbSyncStatsDTO();
            stats.setProdutosSincronizados(0);
            stats.setErros(1);
            stats.setApiDisponivel(false);
            stats.setStatusApi("ERRO: " + e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(stats);
        }
    }
}
