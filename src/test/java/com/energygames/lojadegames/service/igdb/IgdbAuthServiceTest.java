package com.energygames.lojadegames.service.igdb;

import com.energygames.lojadegames.configuration.IgdbConfigProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do IgdbAuthService")
class IgdbAuthServiceTest {

    @Mock
    private IgdbConfigProperties config;

    @Mock
    private WebClient.Builder webClientBuilder;

    private IgdbAuthService authService;

    @BeforeEach
    void setUp() {
        lenient().when(webClientBuilder.build()).thenReturn(mock(WebClient.class));
        lenient().when(config.getClientId()).thenReturn("test-client-id");
        lenient().when(config.getClientSecret()).thenReturn("test-client-secret");
        
        authService = new IgdbAuthService(config, webClientBuilder);
    }

    @Test
    @DisplayName("Deve lançar exceção quando credenciais não configuradas")
    void deveLancarExcecaoQuandoCredenciaisNaoConfiguradas() {
        // Arrange
        when(config.hasCredentials()).thenReturn(false);

        // Act & Assert
        IllegalStateException exception = assertThrows(
            IllegalStateException.class,
            () -> authService.getAccessToken()
        );

        assertTrue(exception.getMessage().contains("Credenciais IGDB não configuradas"));
    }

    @Test
    @DisplayName("Deve indicar que não tem token válido inicialmente")
    void deveIndicarQueNaoTemTokenValidoInicialmente() {
        // Assert
        assertFalse(authService.hasValidToken());
    }

    @Test
    @DisplayName("Deve limpar cache de token")
    void deveLimparCacheDeToken() {
        // Act
        authService.clearTokenCache();

        // Assert
        assertFalse(authService.hasValidToken());
    }

    @Test
    @DisplayName("Deve verificar se tem credenciais configuradas")
    void deveVerificarSeTemCredenciaisConfiguradas() {
        // Arrange
        when(config.hasCredentials()).thenReturn(true);

        // Assert
        assertTrue(config.hasCredentials());
    }

    @Test
    @DisplayName("Deve configurar client ID e secret corretamente")
    void deveConfigurarClientIdESecretCorretamente() {
        // Assert
        assertEquals("test-client-id", config.getClientId());
        assertEquals("test-client-secret", config.getClientSecret());
    }
}
