package com.energygames.lojadegames.service;

import com.energygames.lojadegames.dto.igdb.IgdbGameDTO;
import com.energygames.lojadegames.enums.OrigemEnum;
import com.energygames.lojadegames.model.Produto;
import com.energygames.lojadegames.model.ProdutoOrigemExterna;
import com.energygames.lojadegames.repository.CategoriaRepository;
import com.energygames.lojadegames.repository.ProdutoOrigemExternaRepository;
import com.energygames.lojadegames.repository.ProdutoRepository;
import com.energygames.lojadegames.service.igdb.IgdbApiClient;
import com.energygames.lojadegames.service.igdb.IgdbImportService;
import com.energygames.lojadegames.service.igdb.IgdbMapperService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do IgdbImportService - Lógica de Importação")
class IgdbImportServiceTest {

    @Mock
    private IgdbApiClient apiClient;

    @Mock
    private IgdbMapperService mapperService;

    @Mock
    private ProdutoRepository produtoRepository;

    @Mock
    private ProdutoOrigemExternaRepository origemExternaRepository;

    @Mock
    private CategoriaRepository categoriaRepository;

    @InjectMocks
    private IgdbImportService importService;

    private IgdbGameDTO gameDTO;
    private Produto produto;

    @BeforeEach
    void setUp() {
        gameDTO = new IgdbGameDTO();
        gameDTO.setId(1234L);
        gameDTO.setName("The Legend of Zelda: Breath of the Wild");

        produto = new Produto();
        produto.setId(1L);
        produto.setNome("The Legend of Zelda: Breath of the Wild");
    }

    @Test
    @DisplayName("Deve importar jogo por ID quando não existe no banco")
    void deveImportarJogoPorIdQuandoNaoExiste() {
        // Arrange
        when(origemExternaRepository.findByOrigemAndIdExterno(OrigemEnum.IGDB, "1234"))
            .thenReturn(Optional.empty());
        when(apiClient.getGameById(1234L)).thenReturn(gameDTO);
        when(mapperService.mapGameToProduct(any(), any(), any(), any(), any(), any(), any()))
            .thenReturn(produto);
        when(produtoRepository.save(any(Produto.class))).thenReturn(produto);

        // Act
        Produto resultado = importService.importGameById(1234L);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getNome()).isEqualTo("The Legend of Zelda: Breath of the Wild");
        
        verify(origemExternaRepository).findByOrigemAndIdExterno(OrigemEnum.IGDB, "1234");
        verify(apiClient).getGameById(1234L);
        verify(produtoRepository).save(any(Produto.class));
    }

    @Test
    @DisplayName("Deve retornar produto existente quando jogo já foi importado")
    void deveRetornarProdutoExistenteQuandoJaFoiImportado() {
        // Arrange
        ProdutoOrigemExterna origem = new ProdutoOrigemExterna();
        origem.setProduto(produto);
        
        when(origemExternaRepository.findByOrigemAndIdExterno(OrigemEnum.IGDB, "1234"))
            .thenReturn(Optional.of(origem));

        // Act
        Produto resultado = importService.importGameById(1234L);

        // Assert
        assertThat(resultado).isEqualTo(produto);
        
        verify(origemExternaRepository).findByOrigemAndIdExterno(OrigemEnum.IGDB, "1234");
        verify(apiClient, never()).getGameById(any());
        verify(produtoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando jogo não existe na IGDB")
    void deveLancarExcecaoQuandoJogoNaoExiste() {
        // Arrange
        when(origemExternaRepository.findByOrigemAndIdExterno(OrigemEnum.IGDB, "9999"))
            .thenReturn(Optional.empty());
        when(apiClient.getGameById(9999L)).thenReturn(null);

        // Act & Assert
        assertThatThrownBy(() -> importService.importGameById(9999L))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Jogo IGDB ID 9999 não encontrado");
        
        verify(produtoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve buscar jogos por nome e retornar lista de DTOs")
    void deveBuscarJogosPorNome() {
        // Arrange
        IgdbGameDTO game1 = new IgdbGameDTO();
        game1.setId(1L);
        game1.setName("Zelda BOTW");
        
        IgdbGameDTO game2 = new IgdbGameDTO();
        game2.setId(2L);
        game2.setName("Zelda TOTK");
        
        when(apiClient.searchGamesByName("zelda", 10, 0))
            .thenReturn(List.of(game1, game2));

        // Act
        List<IgdbGameDTO> resultado = importService.searchGamesForImport("zelda", 1, 10);

        // Assert
        assertThat(resultado).hasSize(2);
        assertThat(resultado.get(0).getName()).isEqualTo("Zelda BOTW");
        assertThat(resultado.get(1).getName()).isEqualTo("Zelda TOTK");
        
        verify(apiClient).searchGamesByName("zelda", 10, 0);
    }

    @Test
    @DisplayName("Deve importar jogos populares ignorando duplicatas")
    void deveImportarJogosPopularesIgnorandoDuplicatas() {
        // Arrange
        IgdbGameDTO game1 = new IgdbGameDTO();
        game1.setId(1L);
        game1.setName("Game 1");
        
        IgdbGameDTO game2 = new IgdbGameDTO();
        game2.setId(2L);
        game2.setName("Game 2");
        
        ProdutoOrigemExterna origemExistente = new ProdutoOrigemExterna();
        origemExistente.setProduto(produto);

        when(apiClient.getPopularGames(20, 0)).thenReturn(List.of(game1, game2));
        
        // game1 não existe, game2 já existe
        when(origemExternaRepository.findByOrigemAndIdExterno(OrigemEnum.IGDB, "1"))
            .thenReturn(Optional.empty());
        when(origemExternaRepository.findByOrigemAndIdExterno(OrigemEnum.IGDB, "2"))
            .thenReturn(Optional.of(origemExistente));
        
        Produto novoProduto = new Produto();
        novoProduto.setId(10L);
        novoProduto.setNome("Game 1");
        
        when(apiClient.getGameById(1L)).thenReturn(game1);
        when(mapperService.mapGameToProduct(any(), any(), any(), any(), any(), any(), any()))
            .thenReturn(novoProduto);
        when(produtoRepository.save(any())).thenReturn(novoProduto);

        // Act
        List<Produto> resultado = importService.importPopularGames(20);

        // Assert
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getNome()).isEqualTo("Game 1");
        
        verify(produtoRepository, times(1)).save(any());
    }
}
