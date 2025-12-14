package com.energygames.lojadegames.service.igdb;

import com.energygames.lojadegames.configuration.IgdbConfigProperties;
import com.energygames.lojadegames.dto.igdb.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;

/**
 * Client HTTP para consumir a API da IGDB v4
 * Utiliza WebClient reativo para requisições assíncronas
 * 
 * Documentação oficial: https://api-docs.igdb.com/
 */
@Service
public class IgdbApiClient {

    private static final Logger log = LoggerFactory.getLogger(IgdbApiClient.class);

    private final IgdbConfigProperties config;
    private final IgdbAuthService authService;
    private final WebClient webClient;

    public IgdbApiClient(IgdbConfigProperties config, IgdbAuthService authService, WebClient.Builder webClientBuilder) {
        this.config = config;
        this.authService = authService;
        this.webClient = webClientBuilder
            .baseUrl(config.getApiUrl())
            .defaultHeader("Accept", "application/json")
            .build();
    }

    /**
     * Busca jogos na IGDB por ID
     * @param gameId ID do jogo na IGDB
     * @return Dados do jogo ou null se não encontrado
     */
    public IgdbGameDTO getGameById(Long gameId) {
        String query = String.format("fields *, cover.*, platforms.*, genres.*; where id = %d;", gameId);
        List<IgdbGameDTO> results = searchGames(query);
        return results.isEmpty() ? null : results.get(0);
    }

    /**
     * Busca jogos na IGDB por nome com paginação
     * @param gameName Nome do jogo para buscar
     * @param limit Limite de resultados (máx: 500)
     * @param offset Quantidade de registros para pular
     * @return Lista de jogos encontrados
     */
    public List<IgdbGameDTO> searchGamesByName(String gameName, int limit, int offset) {
        String query = String.format(
            "search \"%s\"; fields *, cover.*, platforms.*, genres.*; limit %d; offset %d;", 
            gameName.replace("\"", "\\\""), 
            Math.min(limit, 500),
            offset
        );
        return searchGames(query);
    }

    /**
     * Busca jogos populares na IGDB com paginação
     * @param limit Limite de resultados
     * @param offset Quantidade de registros para pular
     * @return Lista de jogos populares
     */
    public List<IgdbGameDTO> getPopularGames(int limit, int offset) {
        String query = String.format(
            "fields *, cover.*, platforms.*, genres.*; where rating > 75 & rating_count > 50; sort rating_count desc; limit %d; offset %d;",
            Math.min(limit, 500),
            offset
        );
        return searchGames(query);
    }

    /**
     * Executa query customizada no endpoint /games
     * @param query Query no formato Apicalypse da IGDB
     * @return Lista de jogos
     */
    public List<IgdbGameDTO> searchGames(String query) {
        return executeQuery("/games", query, new ParameterizedTypeReference<List<IgdbGameDTO>>() {});
    }

    /**
     * Busca capa de jogo por ID
     * @param coverId ID da capa na IGDB
     * @return Dados da capa
     */
    public IgdbCoverDTO getCoverById(Long coverId) {
        String query = String.format("fields *; where id = %d;", coverId);
        List<IgdbCoverDTO> results = executeQuery("/covers", query, new ParameterizedTypeReference<List<IgdbCoverDTO>>() {});
        return results.isEmpty() ? null : results.get(0);
    }

    /**
     * Busca screenshots de jogo por IDs
     * @param screenshotIds Lista de IDs de screenshots
     * @return Lista de screenshots
     */
    public List<IgdbScreenshotDTO> getScreenshotsByIds(List<Long> screenshotIds) {
        if (screenshotIds == null || screenshotIds.isEmpty()) return List.of();
        
        String ids = screenshotIds.stream()
            .map(String::valueOf)
            .reduce((a, b) -> a + "," + b)
            .orElse("");
        
        String query = String.format("fields *; where id = (%s);", ids);
        return executeQuery("/screenshots", query, new ParameterizedTypeReference<List<IgdbScreenshotDTO>>() {});
    }

    /**
     * Busca vídeos de jogo por IDs
     * @param videoIds Lista de IDs de vídeos
     * @return Lista de vídeos
     */
    public List<IgdbVideoDTO> getVideosByIds(List<Long> videoIds) {
        if (videoIds == null || videoIds.isEmpty()) return List.of();
        
        String ids = videoIds.stream()
            .map(String::valueOf)
            .reduce((a, b) -> a + "," + b)
            .orElse("");
        
        String query = String.format("fields *; where id = (%s);", ids);
        return executeQuery("/game_videos", query, new ParameterizedTypeReference<List<IgdbVideoDTO>>() {});
    }

    /**
     * Busca plataformas por IDs
     * @param platformIds Lista de IDs de plataformas
     * @return Lista de plataformas
     */
    public List<IgdbPlatformDTO> getPlatformsByIds(List<Long> platformIds) {
        // Método mantido para compatibilidade, mas não usado na importação principal
        // pois os dados já vêm expandidos
        if (platformIds == null || platformIds.isEmpty()) return List.of();
        
        String ids = platformIds.stream()
            .map(String::valueOf)
            .reduce((a, b) -> a + "," + b)
            .orElse("");
        
        String query = String.format("fields *; where id = (%s);", ids);
        return executeQuery("/platforms", query, new ParameterizedTypeReference<List<IgdbPlatformDTO>>() {});
    }

    /**
     * Busca gêneros por IDs
     * @param genreIds Lista de IDs de gêneros
     * @return Lista de gêneros
     */
    public List<IgdbGenreDTO> getGenresByIds(List<Long> genreIds) {
        if (genreIds == null || genreIds.isEmpty()) return List.of();
        
        String ids = genreIds.stream()
            .map(String::valueOf)
            .reduce((a, b) -> a + "," + b)
            .orElse("");
        
        String query = String.format("fields *; where id = (%s);", ids);
        return executeQuery("/genres", query, new ParameterizedTypeReference<List<IgdbGenreDTO>>() {});
    }

    /**
     * Busca TODOS os gêneros disponíveis na IGDB
     * A IGDB tem cerca de 25 gêneros no total
     * @return Lista completa de gêneros
     */
    public List<IgdbGenreDTO> getAllGenres() {
        String query = "fields id, name, slug; limit 100; sort name asc;";
        log.info("Buscando todos os gêneros da IGDB");
        return executeQuery("/genres", query, new ParameterizedTypeReference<List<IgdbGenreDTO>>() {});
    }

    /**
     * Executa query genérica em qualquer endpoint da IGDB
     * @param endpoint Endpoint da API (ex: /games, /covers)
     * @param query Query no formato Apicalypse
     * @param responseType Tipo de resposta esperado
     * @return Resposta deserializada
     */
    private <T> T executeQuery(String endpoint, String query, ParameterizedTypeReference<T> responseType) {
        String accessToken = authService.getAccessToken();
        
        log.debug("Executando query IGDB no endpoint {}: {}", endpoint, query);

        try {
            return webClient.post()
                .uri(endpoint)
                .header("Client-ID", config.getClientId())
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.TEXT_PLAIN)
                .bodyValue(query)
                .retrieve()
                .onStatus(
                    status -> status.is4xxClientError() || status.is5xxServerError(),
                    clientResponse -> {
                        log.error("Erro na requisição IGDB {}. Status: {}", endpoint, clientResponse.statusCode());
                        return Mono.error(new RuntimeException("Erro na API IGDB: " + clientResponse.statusCode()));
                    }
                )
                .bodyToMono(responseType)
                .timeout(Duration.ofSeconds(config.getRequestTimeoutSeconds()))
                .block();

        } catch (Exception e) {
            log.error("Erro ao executar query IGDB no endpoint {}: {}", endpoint, e.getMessage(), e);
            throw new RuntimeException("Falha ao consultar IGDB API: " + e.getMessage(), e);
        }
    }

    /**
     * Verifica se a API está acessível
     * @return true se API responde corretamente
     */
    public boolean isApiAvailable() {
        try {
            List<IgdbGameDTO> result = searchGames("fields id; limit 1;");
            return result != null;
        } catch (Exception e) {
            log.warn("API IGDB não disponível: {}", e.getMessage());
            return false;
        }
    }
}
