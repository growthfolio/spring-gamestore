package com.energygames.lojadegames.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configurações para integração com a API da IGDB
 * Requer credenciais obtidas no Twitch Developer Portal
 * 
 * Como obter credenciais:
 * 1. Acesse https://dev.twitch.tv/console/apps
 * 2. Crie uma nova aplicação
 * 3. Defina uma categoria (ex: "Application Integration")
 * 4. Copie o Client ID
 * 5. Gere um Client Secret
 * 6. Configure no application.properties
 */
@Configuration
@ConfigurationProperties(prefix = "igdb")
public class IgdbConfigProperties {

    /**
     * URL base da API IGDB v4
     */
    private String apiUrl = "https://api.igdb.com/v4";

    /**
     * URL de autenticação do Twitch OAuth2
     */
    private String authUrl = "https://id.twitch.tv/oauth2/token";

    /**
     * Client ID obtido no Twitch Developer Portal
     */
    private String clientId;

    /**
     * Client Secret obtido no Twitch Developer Portal
     */
    private String clientSecret;

    /**
     * Habilitar/desabilitar sincronização automática
     */
    private boolean syncEnabled = false;

    /**
     * Intervalo de sincronização em dias (padrão: 7 dias)
     */
    private int syncIntervalDays = 7;

    /**
     * Número máximo de jogos a importar por requisição (padrão: 10)
     */
    private int importBatchSize = 10;

    /**
     * Timeout para requisições à API em segundos (padrão: 10s)
     */
    private int requestTimeoutSeconds = 10;

    // Getters e Setters

    public String getApiUrl() {
        return apiUrl;
    }

    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    public String getAuthUrl() {
        return authUrl;
    }

    public void setAuthUrl(String authUrl) {
        this.authUrl = authUrl;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public boolean isSyncEnabled() {
        return syncEnabled;
    }

    public void setSyncEnabled(boolean syncEnabled) {
        this.syncEnabled = syncEnabled;
    }

    public int getSyncIntervalDays() {
        return syncIntervalDays;
    }

    public void setSyncIntervalDays(int syncIntervalDays) {
        this.syncIntervalDays = syncIntervalDays;
    }

    public int getImportBatchSize() {
        return importBatchSize;
    }

    public void setImportBatchSize(int importBatchSize) {
        this.importBatchSize = importBatchSize;
    }

    public int getRequestTimeoutSeconds() {
        return requestTimeoutSeconds;
    }

    public void setRequestTimeoutSeconds(int requestTimeoutSeconds) {
        this.requestTimeoutSeconds = requestTimeoutSeconds;
    }

    /**
     * Valida se as credenciais foram configuradas
     */
    public boolean hasCredentials() {
        return clientId != null && !clientId.isEmpty() 
            && clientSecret != null && !clientSecret.isEmpty();
    }
}
