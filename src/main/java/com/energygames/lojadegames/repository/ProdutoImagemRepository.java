package com.energygames.lojadegames.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.energygames.lojadegames.enums.TipoImagemEnum;
import com.energygames.lojadegames.model.ProdutoImagem;

/**
 * Repository para operações de banco de dados da entidade ProdutoImagem.
 */
@Repository
public interface ProdutoImagemRepository extends JpaRepository<ProdutoImagem, Long> {

    /**
     * Busca todas as imagens de um produto
     */
    List<ProdutoImagem> findByProdutoId(Long produtoId);

    /**
     * Busca todas as imagens de um produto ordenadas
     */
    List<ProdutoImagem> findByProdutoIdOrderByOrdemAsc(Long produtoId);

    /**
     * Busca imagens de um produto por tipo
     */
    List<ProdutoImagem> findByProdutoIdAndTipo(Long produtoId, TipoImagemEnum tipo);

    /**
     * Busca imagens de um produto por tipo ordenadas
     */
    List<ProdutoImagem> findByProdutoIdAndTipoOrderByOrdemAsc(Long produtoId, TipoImagemEnum tipo);

    /**
     * Busca a imagem principal de um produto
     */
    @Query("SELECT pi FROM ProdutoImagem pi WHERE pi.produto.id = :produtoId AND pi.imagemPrincipal = true")
    ProdutoImagem findImagemPrincipalByProdutoId(@Param("produtoId") Long produtoId);

    /**
     * Busca todas as capas
     */
    List<ProdutoImagem> findByTipo(TipoImagemEnum tipo);

    /**
     * Conta imagens de um produto
     */
    Long countByProdutoId(Long produtoId);

    /**
     * Conta imagens de um produto por tipo
     */
    Long countByProdutoIdAndTipo(Long produtoId, TipoImagemEnum tipo);

    /**
     * Deleta todas as imagens de um produto
     */
    void deleteByProdutoId(Long produtoId);
}
