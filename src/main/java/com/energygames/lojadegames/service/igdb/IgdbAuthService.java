package com.energygames.lojadegames.service.igdb;

import com.energygames.lojadegames.configuration.IgdbConfigProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

/**
 * Serviço de autenticação OAuth2 para IGDB via Twitch
 * Gerencia tokens de acesso com cache e renovação automática
 */
@Service
public class IgdbAuthService {

    private static final Logger log = LoggerFactory.getLogger(IgdbAuthService.class);

    private final IgdbConfigProperties config;
    private final WebClient webClient;

    // Cache do token
    private String cachedAccessToken;
    private LocalDateTime tokenExpirationTime;

    public IgdbAuthService(IgdbConfigProperties config, WebClient.Builder webClientBuilder) {
        this.config = config;
        this.webClient = webClientBuilder.build();
    }

    /**
     * Obtém token de acesso válido (usa cache se disponível)
     * @return Access token válido
     * @throws IllegalStateException se credenciais não configuradas
     */
    public String getAccessToken() {
        if (!config.hasCredentials()) {
            throw new IllegalStateException(
                "Credenciais IGDB não configuradas. Configure igdb.client-id e igdb.client-secret"
            );
        }

        // Verifica se token está em cache e ainda válido (com 5 min de margem)
        if (cachedAccessToken != null && tokenExpirationTime != null 
            && LocalDateTime.now().plusMinutes(5).isBefore(tokenExpirationTime)) {
            log.debug("Usando token IGDB em cache (expira em: {})", tokenExpirationTime);
            return cachedAccessToken;
        }

        // Token expirado ou inexistente, renovar
        log.info("Solicitando novo token de acesso IGDB via Twitch OAuth2");
        return refreshAccessToken();
    }

    /**
     * Força renovação do token de acesso
     * @return Novo access token
     */
    public String refreshAccessToken() {
        try {
            TwitchTokenResponse response = webClient.post()
                .uri(uriBuilder -> uriBuilder
                    .scheme("https")
                    .host("id.twitch.tv")
                    .path("/oauth2/token")
                    .queryParam("client_id", config.getClientId())
                    .queryParam("client_secret", config.getClientSecret())
                    .queryParam("grant_type", "client_credentials")
                    .build())
                .contentType(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(
                    status -> status.is4xxClientError() || status.is5xxServerError(),
                    clientResponse -> {
                        log.error("Erro ao autenticar com Twitch OAuth2. Status: {}", clientResponse.statusCode());
                        return Mono.error(new RuntimeException("Falha na autenticação IGDB: " + clientResponse.statusCode()));
                    }
                )
                .bodyToMono(TwitchTokenResponse.class)
                .block();

            if (response == null || response.accessToken == null) {
                throw new RuntimeException("Resposta de autenticação IGDB inválida");
            }

            // Atualiza cache
            cachedAccessToken = response.accessToken;
            tokenExpirationTime = LocalDateTime.now().plusSeconds(response.expiresIn);

            log.info("Token IGDB obtido com sucesso. Expira em: {} ({} segundos)", 
                tokenExpirationTime, response.expiresIn);

            return cachedAccessToken;

        } catch (Exception e) {
            log.error("Erro ao obter token de acesso IGDB", e);
            throw new RuntimeException("Não foi possível autenticar com IGDB API: " + e.getMessage(), e);
        }
    }

    /**
     * Limpa cache de token (útil para testes ou forçar renovação)
     */
    public void clearTokenCache() {
        log.info("Cache de token IGDB limpo");
        cachedAccessToken = null;
        tokenExpirationTime = null;
    }

    /**
     * Verifica se token está válido
     */
    public boolean hasValidToken() {
        return cachedAccessToken != null 
            && tokenExpirationTime != null 
            && LocalDateTime.now().isBefore(tokenExpirationTime);
    }

    /**
     * DTO para resposta do Twitch OAuth2
     */
    private static class TwitchTokenResponse {
        @JsonProperty("access_token")
        private String accessToken;

        @JsonProperty("expires_in")
        private Integer expiresIn; // Segundos até expiração (normalmente 5184000 = 60 dias)

        @JsonProperty("token_type")
        private String tokenType; // "bearer"

        public String getAccessToken() {
            return accessToken;
        }

        public void setAccessToken(String accessToken) {
            this.accessToken = accessToken;
        }

        public Integer getExpiresIn() {
            return expiresIn;
        }

        public void setExpiresIn(Integer expiresIn) {
            this.expiresIn = expiresIn;
        }

        public String getTokenType() {
            return tokenType;
        }

        public void setTokenType(String tokenType) {
            this.tokenType = tokenType;
        }
    }
}
