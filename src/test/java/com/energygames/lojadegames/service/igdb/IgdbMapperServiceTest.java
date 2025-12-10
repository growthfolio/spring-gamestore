package com.energygames.lojadegames.service.igdb;

import com.energygames.lojadegames.dto.igdb.*;
import com.energygames.lojadegames.enums.OrigemEnum;
import com.energygames.lojadegames.enums.StatusJogoEnum;
import com.energygames.lojadegames.enums.TipoImagemEnum;
import com.energygames.lojadegames.enums.TipoVideoEnum;
import com.energygames.lojadegames.model.*;
import com.energygames.lojadegames.repository.CategoriaRepository;
import com.energygames.lojadegames.repository.PlataformaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do IgdbMapperService")
class IgdbMapperServiceTest {

    @Mock
    private PlataformaRepository plataformaRepository;

    @Mock
    private CategoriaRepository categoriaRepository;

    @InjectMocks
    private IgdbMapperService mapperService;

    private IgdbGameDTO gameDTO;
    private IgdbCoverDTO coverDTO;
    private List<IgdbScreenshotDTO> screenshots;
    private List<IgdbVideoDTO> videos;
    private List<IgdbPlatformDTO> platforms;
    private List<IgdbGenreDTO> genres;

    @BeforeEach
    void setUp() {
        // Game DTO básico
        gameDTO = new IgdbGameDTO();
        gameDTO.setId(1234L);
        gameDTO.setName("The Legend of Zelda: Breath of the Wild");
        gameDTO.setSlug("the-legend-of-zelda-breath-of-the-wild");
        gameDTO.setSummary("Explore o vasto reino de Hyrule nesta aventura épica.");
        gameDTO.setStoryline("Link acorda após 100 anos para derrotar Calamity Ganon...");
        gameDTO.setFirstReleaseDate(1488326400L); // 01/03/2017
        gameDTO.setTotalRating(new BigDecimal("97.5")); // 0-100
        gameDTO.setTotalRatingCount(1250);
        gameDTO.setStatus(0); // Released

        // Cover
        coverDTO = new IgdbCoverDTO();
        coverDTO.setId(100L);
        coverDTO.setImageId("co1234");
        coverDTO.setWidth(264);
        coverDTO.setHeight(374);

        // Screenshot
        IgdbScreenshotDTO screenshot = new IgdbScreenshotDTO();
        screenshot.setId(200L);
        screenshot.setImageId("sc5678");
        screenshot.setWidth(1920);
        screenshot.setHeight(1080);
        screenshots = List.of(screenshot);

        // Video
        IgdbVideoDTO video = new IgdbVideoDTO();
        video.setId(300L);
        video.setVideoId("abc123xyz");
        video.setName("Official Trailer");
        videos = List.of(video);

        // Platform
        IgdbPlatformDTO platform = new IgdbPlatformDTO();
        platform.setId(130L); // Nintendo Switch
        platform.setName("Nintendo Switch");
        platform.setSlug("switch");
        platform.setAbbreviation("NSW");
        platforms = List.of(platform);

        // Genre
        IgdbGenreDTO genre = new IgdbGenreDTO();
        genre.setId(12L);
        genre.setName("Adventure");
        genre.setSlug("adventure");
        genres = List.of(genre);
    }

    @Test
    @DisplayName("Deve mapear jogo IGDB completo para Produto")
    void deveMapearJogoCompletoParaProduto() {
        // Arrange
        Plataforma plataforma = new Plataforma();
        plataforma.setId(1L);
        plataforma.setNome("Nintendo Switch");
        when(plataformaRepository.findByIdIgdb(130)).thenReturn(Optional.of(plataforma));

        Categoria categoria = new Categoria();
        categoria.setId(1L);
        categoria.setTipo("Adventure");
        when(categoriaRepository.findByIdIgdb(12)).thenReturn(Optional.of(categoria));

        // Act
        Produto produto = mapperService.mapGameToProduct(
            gameDTO, coverDTO, screenshots, videos, platforms, genres
        );

        // Assert
        assertNotNull(produto);
        assertEquals("The Legend of Zelda: Breath of the Wild", produto.getNome());
        assertEquals("the-legend-of-zelda-breath-of-the-wild", produto.getSlug());
        assertEquals("Explore o vasto reino de Hyrule nesta aventura épica.", produto.getDescricao());
        assertEquals("Link acorda após 100 anos para derrotar Calamity Ganon...", produto.getDescricaoCompleta());
        
        // Data de lançamento
        LocalDate expectedDate = Instant.ofEpochSecond(1488326400L)
            .atZone(ZoneId.systemDefault())
            .toLocalDate();
        assertEquals(expectedDate, produto.getDataLancamento());

        // Rating convertido (97.5 / 10 = 9.75)
        assertEquals(new BigDecimal("9.75"), produto.getRatingIgdb());
        assertEquals(1250, produto.getTotalVotosExternos());

        // Status
        assertEquals(StatusJogoEnum.RELEASED, produto.getStatus());
        assertTrue(produto.getAtivo()); // Released = ativo

        // Origem externa
        assertNotNull(produto.getOrigemExterna());
        assertEquals(OrigemEnum.IGDB, produto.getOrigemExterna().getOrigem());
        assertEquals("1234", produto.getOrigemExterna().getIdExterno());
        assertTrue(produto.getOrigemExterna().getSincronizacaoAtiva());

        verify(plataformaRepository).findByIdIgdb(130);
        verify(categoriaRepository).findByIdIgdb(12);
    }

    @Test
    @DisplayName("Deve mapear imagens corretamente (capa + screenshots)")
    void deveMapearImagensCorretamente() {
        // Act
        Produto produto = mapperService.mapGameToProduct(
            gameDTO, coverDTO, screenshots, videos, null, null
        );

        // Assert
        assertNotNull(produto.getImagensEstruturadas());
        assertEquals(2, produto.getImagensEstruturadas().size()); // 1 capa + 1 screenshot

        // Verificar capa
        Optional<ProdutoImagem> capa = produto.getImagensEstruturadas().stream()
            .filter(img -> img.getTipo() == TipoImagemEnum.CAPA)
            .findFirst();
        
        assertTrue(capa.isPresent());
        assertTrue(capa.get().getImagemPrincipal());
        assertEquals("https://images.igdb.com/igdb/image/upload/t_cover_big/co1234.jpg", capa.get().getUrl());
        assertEquals(264, capa.get().getLargura());
        assertEquals(374, capa.get().getAltura());
        assertEquals("100", capa.get().getIdIgdb());

        // Verificar screenshot
        Optional<ProdutoImagem> screenshot = produto.getImagensEstruturadas().stream()
            .filter(img -> img.getTipo() == TipoImagemEnum.SCREENSHOT)
            .findFirst();
        
        assertTrue(screenshot.isPresent());
        assertFalse(screenshot.get().getImagemPrincipal());
        assertEquals("https://images.igdb.com/igdb/image/upload/t_screenshot_big/sc5678.jpg", screenshot.get().getUrl());
    }

    @Test
    @DisplayName("Deve mapear vídeos corretamente")
    void deveMapearVideosCorretamente() {
        // Act
        Produto produto = mapperService.mapGameToProduct(
            gameDTO, coverDTO, null, videos, null, null
        );

        // Assert
        assertNotNull(produto.getVideos());
        assertEquals(1, produto.getVideos().size());

        ProdutoVideo video = produto.getVideos().iterator().next();
        assertEquals("abc123xyz", video.getVideoId());
        assertEquals("Official Trailer", video.getTitulo());
        assertEquals(TipoVideoEnum.TRAILER, video.getTipo());
        assertEquals(1, video.getOrdem());
    }

    @Test
    @DisplayName("Deve mapear plataforma existente no banco")
    void deveMapearPlataformaExistente() {
        // Arrange
        Plataforma plataforma = new Plataforma();
        plataforma.setId(1L);
        plataforma.setNome("Nintendo Switch");
        plataforma.setIdIgdb(130);
        when(plataformaRepository.findByIdIgdb(130)).thenReturn(Optional.of(plataforma));

        // Act
        Produto produto = mapperService.mapGameToProduct(
            gameDTO, null, null, null, platforms, null
        );

        // Assert
        assertNotNull(produto.getPlataformas());
        assertEquals(1, produto.getPlataformas().size());
        assertTrue(produto.getPlataformas().contains(plataforma));
        verify(plataformaRepository).findByIdIgdb(130);
    }

    @Test
    @DisplayName("Deve criar nova categoria quando gênero não existe")
    void deveCriarNovaCategoriaNaoExistente() {
        // Arrange
        when(categoriaRepository.findByIdIgdb(12)).thenReturn(Optional.empty());

        // Act
        Produto produto = mapperService.mapGameToProduct(
            gameDTO, null, null, null, null, genres
        );

        // Assert
        assertNotNull(produto.getGeneros());
        assertEquals(1, produto.getGeneros().size());
        
        Categoria categoria = produto.getGeneros().iterator().next();
        assertEquals("Adventure", categoria.getTipo());
        assertEquals("adventure", categoria.getSlug());
        assertEquals(12, categoria.getIdIgdb());
        assertEquals("Gênero importado da IGDB", categoria.getDescricao());
        assertTrue(categoria.getAtivo());

        verify(categoriaRepository).findByIdIgdb(12);
    }

    @Test
    @DisplayName("Deve converter status IGDB corretamente")
    void deveConverterStatusIgdbCorretamente() {
        // Test Released
        gameDTO.setStatus(0);
        Produto produtoReleased = mapperService.mapGameToProduct(gameDTO, null, null, null, null, null);
        assertEquals(StatusJogoEnum.RELEASED, produtoReleased.getStatus());
        assertTrue(produtoReleased.getAtivo());

        // Test Alpha
        gameDTO.setStatus(2);
        Produto produtoAlpha = mapperService.mapGameToProduct(gameDTO, null, null, null, null, null);
        assertEquals(StatusJogoEnum.ALPHA, produtoAlpha.getStatus());
        assertFalse(produtoAlpha.getAtivo());

        // Test Beta
        gameDTO.setStatus(3);
        Produto produtoBeta = mapperService.mapGameToProduct(gameDTO, null, null, null, null, null);
        assertEquals(StatusJogoEnum.BETA, produtoBeta.getStatus());

        // Test Early Access
        gameDTO.setStatus(4);
        Produto produtoEarlyAccess = mapperService.mapGameToProduct(gameDTO, null, null, null, null, null);
        assertEquals(StatusJogoEnum.EARLY_ACCESS, produtoEarlyAccess.getStatus());

        // Test Cancelled
        gameDTO.setStatus(6);
        Produto produtoCancelled = mapperService.mapGameToProduct(gameDTO, null, null, null, null, null);
        assertEquals(StatusJogoEnum.CANCELLED, produtoCancelled.getStatus());
    }

    @Test
    @DisplayName("Deve lidar com dados opcionais ausentes")
    void deveLidarComDadosOpcionaisAusentes() {
        // Arrange - game sem dados opcionais
        gameDTO.setFirstReleaseDate(null);
        gameDTO.setTotalRating(null);
        gameDTO.setTotalRatingCount(null);
        gameDTO.setStoryline(null);

        // Act
        Produto produto = mapperService.mapGameToProduct(
            gameDTO, null, null, null, null, null
        );

        // Assert
        assertNotNull(produto);
        assertEquals("The Legend of Zelda: Breath of the Wild", produto.getNome());
        assertNull(produto.getDataLancamento());
        assertNull(produto.getRatingIgdb());
        assertEquals(0, produto.getTotalVotosExternos());
        assertEquals(gameDTO.getSummary(), produto.getDescricaoCompleta()); // Usa summary se storyline null
    }

    @Test
    @DisplayName("Deve truncar descrição quando excede limite")
    void deveTruncarDescricaoQuandoExcedeLimite() {
        // Arrange
        String descricaoGrande = "A".repeat(600); // 600 caracteres (limite é 500)
        gameDTO.setSummary(descricaoGrande);

        // Act
        Produto produto = mapperService.mapGameToProduct(
            gameDTO, null, null, null, null, null
        );

        // Assert
        assertEquals(500, produto.getDescricao().length());
        assertTrue(produto.getDescricao().endsWith("...")); // Truncado com reticências
    }

    @Test
    @DisplayName("Deve mapear links externos (websites e external games)")
    void deveMapearLinksExternos() {
        // Arrange
        IgdbExternalGameDTO steamLink = new IgdbExternalGameDTO();
        steamLink.setId(1L);
        steamLink.setCategory(1); // Steam
        steamLink.setUrl("https://store.steampowered.com/app/123");
        gameDTO.setExternalGames(List.of(steamLink));

        IgdbWebsiteDTO officialSite = new IgdbWebsiteDTO();
        officialSite.setId(2L);
        officialSite.setCategory(1); // Site Oficial
        officialSite.setUrl("https://zelda.com");
        gameDTO.setWebsites(List.of(officialSite));

        // Act
        Produto produto = mapperService.mapGameToProduct(
            gameDTO, null, null, null, null, null
        );

        // Assert
        assertNotNull(produto.getLinksExternos());
        assertEquals(2, produto.getLinksExternos().size());
        assertEquals("https://store.steampowered.com/app/123", produto.getLinksExternos().get("Steam"));
        assertEquals("https://zelda.com", produto.getLinksExternos().get("Site Oficial"));
    }

    @Test
    @DisplayName("Deve configurar preço inicial como zero")
    void deveConfigurarPrecoInicialComoZero() {
        // Act
        Produto produto = mapperService.mapGameToProduct(
            gameDTO, null, null, null, null, null
        );

        // Assert
        assertEquals(BigDecimal.ZERO, produto.getPreco());
    }

    @Test
    @DisplayName("Deve mapear múltiplas plataformas")
    void deveMapearMultiplasPlataformas() {
        // Arrange
        IgdbPlatformDTO nintendoSwitch = new IgdbPlatformDTO();
        nintendoSwitch.setId(130L);
        nintendoSwitch.setName("Nintendo Switch");

        IgdbPlatformDTO ps5 = new IgdbPlatformDTO();
        ps5.setId(167L);
        ps5.setName("PlayStation 5");

        IgdbPlatformDTO xboxSeries = new IgdbPlatformDTO();
        xboxSeries.setId(169L);
        xboxSeries.setName("Xbox Series X|S");

        List<IgdbPlatformDTO> multiplePlatforms = List.of(nintendoSwitch, ps5, xboxSeries);

        Plataforma plataformaSwitch = new Plataforma();
        plataformaSwitch.setId(1L);
        plataformaSwitch.setNome("Nintendo Switch");

        Plataforma plataformaPS5 = new Plataforma();
        plataformaPS5.setId(2L);
        plataformaPS5.setNome("PlayStation 5");

        Plataforma plataformaXbox = new Plataforma();
        plataformaXbox.setId(3L);
        plataformaXbox.setNome("Xbox Series X|S");

        when(plataformaRepository.findByIdIgdb(130)).thenReturn(Optional.of(plataformaSwitch));
        when(plataformaRepository.findByIdIgdb(167)).thenReturn(Optional.of(plataformaPS5));
        when(plataformaRepository.findByIdIgdb(169)).thenReturn(Optional.of(plataformaXbox));

        // Act
        Produto produto = mapperService.mapGameToProduct(
            gameDTO, null, null, null, multiplePlatforms, null
        );

        // Assert
        assertNotNull(produto.getPlataformas());
        assertEquals(3, produto.getPlataformas().size());
        verify(plataformaRepository, times(3)).findByIdIgdb(any());
    }
}
