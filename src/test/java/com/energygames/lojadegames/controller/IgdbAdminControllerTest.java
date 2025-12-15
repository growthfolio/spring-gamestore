package com.energygames.lojadegames.controller;

import com.energygames.lojadegames.configuration.IgdbConfigProperties;
import com.energygames.lojadegames.dto.igdb.IgdbGameDTO;
import com.energygames.lojadegames.enums.OrigemEnum;
import com.energygames.lojadegames.model.Produto;
import com.energygames.lojadegames.model.ProdutoOrigemExterna;
import com.energygames.lojadegames.repository.ProdutoOrigemExternaRepository;
import com.energygames.lojadegames.service.igdb.IgdbApiClient;
import com.energygames.lojadegames.service.igdb.IgdbAuthService;
import com.energygames.lojadegames.service.igdb.IgdbImportService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(IgdbAdminController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("Testes do IgdbAdminController - Endpoints REST")
class IgdbAdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IgdbImportService importService;

    @MockBean
    private IgdbApiClient apiClient;

    @MockBean
    private IgdbAuthService authService;

    @MockBean
    private ProdutoOrigemExternaRepository origemExternaRepository;

    @MockBean
    private IgdbConfigProperties igdbConfigProperties;

    @MockBean
    private com.energygames.lojadegames.repository.ProdutoRepository produtoRepository;

    @MockBean
    private com.energygames.lojadegames.scheduler.IgdbSyncScheduler syncScheduler;

    @MockBean
    private com.energygames.lojadegames.repository.CategoriaRepository categoriaRepository;

    private Produto produto;
    private IgdbGameDTO gameDTO;

    @BeforeEach
    void setUp() {
        produto = new Produto();
        produto.setId(1L);
        produto.setNome("The Legend of Zelda: Breath of the Wild");

        gameDTO = new IgdbGameDTO();
        gameDTO.setId(1234L);
        gameDTO.setName("The Legend of Zelda: Breath of the Wild");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("POST /admin/igdb/import/{id} - Deve importar jogo com sucesso")
    void deveImportarJogoComSucesso() throws Exception {
        // Arrange
        when(importService.importGameById(1234L)).thenReturn(produto);

        // Act & Assert
        mockMvc.perform(post("/admin/igdb/import/{id}", 1234L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.mensagem").value("Jogo importado com sucesso"))
                .andExpect(jsonPath("$.produto.id").value(1))
                .andExpect(jsonPath("$.produto.nome").value("The Legend of Zelda: Breath of the Wild"));

        verify(importService).importGameById(1234L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("POST /admin/igdb/import/{id} - Deve retornar erro 400 quando jogo não encontrado")
    void deveRetornarErro400QuandoJogoNaoEncontrado() throws Exception {
        // Arrange
        when(importService.importGameById(9999L))
            .thenThrow(new IllegalArgumentException("Jogo IGDB ID 9999 não encontrado"));

        // Act & Assert
        mockMvc.perform(post("/admin/igdb/import/{id}", 9999L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem").value("Jogo IGDB ID 9999 não encontrado"));

        verify(importService).importGameById(9999L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("POST /admin/igdb/import/popular - Deve importar jogos populares")
    void deveImportarJogosPopulares() throws Exception {
        // Arrange
        when(importService.importPopularGames(20)).thenReturn(List.of(produto));

        // Act & Assert
        mockMvc.perform(post("/admin/igdb/import/popular")
                .param("quantity", "20")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.mensagem").value("1 jogos importados com sucesso"))
                .andExpect(jsonPath("$.produtos", hasSize(1)));

        verify(importService).importPopularGames(20);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("GET /admin/igdb/search - Deve buscar jogos por termo")
    void deveBuscarJogosPorTermo() throws Exception {
        // Arrange
        when(importService.searchGamesForImport("zelda", 1, 10))
            .thenReturn(List.of(gameDTO));

        // Act & Assert
        mockMvc.perform(get("/admin/igdb/search")
                .param("nome", "zelda")
                .param("limit", "10")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].igdbId").value(1234))
                .andExpect(jsonPath("$[0].nome").value("The Legend of Zelda: Breath of the Wild"));

        verify(importService).searchGamesForImport("zelda", 1, 10);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("GET /admin/igdb/produtos - Deve listar produtos importados da IGDB")
    void deveListarProdutosImportados() throws Exception {
        // Arrange
        ProdutoOrigemExterna origem = new ProdutoOrigemExterna();
        origem.setProduto(produto);
        
        when(origemExternaRepository.findByOrigem(OrigemEnum.IGDB))
            .thenReturn(List.of(origem));

        // Act & Assert
        mockMvc.perform(get("/admin/igdb/produtos")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].nome").value("The Legend of Zelda: Breath of the Wild"));

        verify(origemExternaRepository).findByOrigem(OrigemEnum.IGDB);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("GET /admin/igdb/status - Deve retornar status da integração")
    void deveRetornarStatusDaIntegracao() throws Exception {
        // Arrange
        when(authService.hasValidToken()).thenReturn(true);
        when(origemExternaRepository.findByOrigem(OrigemEnum.IGDB))
            .thenReturn(List.of(new ProdutoOrigemExterna()));

        // Act & Assert
        mockMvc.perform(get("/admin/igdb/status")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tokenValido").value(true))
                .andExpect(jsonPath("$.totalProdutosIgdb").value(1));

        verify(authService).hasValidToken();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("POST /admin/igdb/auth/clear - Deve limpar cache de autenticação")
    void deveLimparCacheDeAutenticacao() throws Exception {
        // Arrange
        doNothing().when(authService).clearTokenCache();

        // Act & Assert
        mockMvc.perform(post("/admin/igdb/auth/clear")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensagem").value("Cache de autenticação limpo"));

        verify(authService).clearTokenCache();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("POST /admin/igdb/import/batch - Deve importar múltiplos jogos em lote")
    void deveImportarJogosEmLote() throws Exception {
        // Arrange
        Produto produto2 = new Produto();
        produto2.setId(2L);
        produto2.setNome("Mario Kart 8");
        
        when(importService.importGamesBatch(List.of(1234L, 5678L)))
            .thenReturn(List.of(produto, produto2));

        String requestBody = """
            {
                "igdbIds": [1234, 5678]
            }
            """;

        // Act & Assert
        mockMvc.perform(post("/admin/igdb/import/batch")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalSolicitado").value(2))
                .andExpect(jsonPath("$.totalProcessado").value(2))
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.resultados", hasSize(2)));

        verify(importService).importGamesBatch(List.of(1234L, 5678L));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("POST /admin/igdb/import/batch - Deve retornar erro 400 com lista vazia")
    void deveRetornarErro400ComListaVazia() throws Exception {
        // Arrange
        String requestBody = """
            {
                "igdbIds": []
            }
            """;

        // Act & Assert
        mockMvc.perform(post("/admin/igdb/import/batch")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("POST /admin/igdb/import/batch - Deve processar lote com falhas parciais")
    void deveProcessarLoteComFalhasParciais() throws Exception {
        // Arrange
        when(importService.importGamesBatch(List.of(1234L, 9999L)))
            .thenReturn(List.of(produto, null)); // null indica falha

        String requestBody = """
            {
                "igdbIds": [1234, 9999]
            }
            """;

        // Act & Assert
        mockMvc.perform(post("/admin/igdb/import/batch")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalSolicitado").value(2))
                .andExpect(jsonPath("$.totalProcessado").value(2))
                .andExpect(jsonPath("$.sucessos").value(greaterThanOrEqualTo(0)))
                .andExpect(jsonPath("$.falhas").value(greaterThanOrEqualTo(0)))
                .andExpect(jsonPath("$.status").value(anyOf(is("PARCIAL"), is("CONCLUIDO"))));

        verify(importService).importGamesBatch(List.of(1234L, 9999L));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("GET /admin/igdb/preview/{id} - Deve retornar preview detalhado do jogo")
    void deveRetornarPreviewDetalhadoDoJogo() throws Exception {
        // Arrange
        gameDTO.setSummary("Uma aventura épica em mundo aberto");
        gameDTO.setStoryline("Link desperta após 100 anos...");
        
        when(importService.getGameDetails(1234L)).thenReturn(gameDTO);

        // Act & Assert
        mockMvc.perform(get("/admin/igdb/preview/{id}", 1234L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.igdbId").value(1234))
                .andExpect(jsonPath("$.nome").value("The Legend of Zelda: Breath of the Wild"))
                .andExpect(jsonPath("$.descricao").value("Uma aventura épica em mundo aberto"))
                .andExpect(jsonPath("$.storyline").value("Link desperta após 100 anos..."));

        verify(importService).getGameDetails(1234L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("GET /admin/igdb/preview/{id} - Deve retornar 404 quando jogo não encontrado")
    void deveRetornar404QuandoJogoNaoEncontradoNoPreview() throws Exception {
        // Arrange
        when(importService.getGameDetails(9999L)).thenReturn(null);

        // Act & Assert
        mockMvc.perform(get("/admin/igdb/preview/{id}", 9999L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(importService).getGameDetails(9999L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("GET /admin/igdb/search - Deve buscar jogos com ordenação por rating descendente")
    void deveBuscarJogosComOrdenacaoPorRating() throws Exception {
        // Arrange
        IgdbGameDTO game1 = new IgdbGameDTO();
        game1.setId(1L);
        game1.setName("Game A");
        
        IgdbGameDTO game2 = new IgdbGameDTO();
        game2.setId(2L);
        game2.setName("Game B");
        
        when(importService.searchGamesForImport("test", 1, 10))
            .thenReturn(List.of(game1, game2));

        // Act & Assert
        mockMvc.perform(get("/admin/igdb/search")
                .param("nome", "test")
                .param("sortBy", "rating")
                .param("sortDir", "desc")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));

        verify(importService).searchGamesForImport("test", 1, 10);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("GET /admin/igdb/search - Deve buscar jogos com ordenação por nome ascendente")
    void deveBuscarJogosComOrdenacaoPorNome() throws Exception {
        // Arrange
        IgdbGameDTO game1 = new IgdbGameDTO();
        game1.setId(1L);
        game1.setName("Zelda");
        
        IgdbGameDTO game2 = new IgdbGameDTO();
        game2.setId(2L);
        game2.setName("Mario");
        
        when(importService.searchGamesForImport("game", 1, 10))
            .thenReturn(List.of(game1, game2));

        // Act & Assert
        mockMvc.perform(get("/admin/igdb/search")
                .param("nome", "game")
                .param("sortBy", "nome")
                .param("sortDir", "asc")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].nome").value("Mario")) // Mario vem antes de Zelda
                .andExpect(jsonPath("$[1].nome").value("Zelda"));

        verify(importService).searchGamesForImport("game", 1, 10);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("GET /admin/igdb/search - Deve buscar jogos com ordenação por status de importação")
    void deveBuscarJogosComOrdenacaoPorStatusImportacao() throws Exception {
        // Arrange
        IgdbGameDTO game1 = new IgdbGameDTO();
        game1.setId(1L);
        game1.setName("Importado");
        
        IgdbGameDTO game2 = new IgdbGameDTO();
        game2.setId(2L);
        game2.setName("Não Importado");
        
        when(importService.searchGamesForImport("test", 1, 10))
            .thenReturn(List.of(game1, game2));

        // Act & Assert
        mockMvc.perform(get("/admin/igdb/search")
                .param("nome", "test")
                .param("sortBy", "importado")
                .param("sortDir", "asc")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));

        verify(importService).searchGamesForImport("test", 1, 10);
    }
}
