package com.energygames.lojadegames.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.energygames.lojadegames.enums.TipoVideoEnum;
import com.energygames.lojadegames.model.ProdutoVideo;

/**
 * Repository para operações de banco de dados da entidade ProdutoVideo.
 */
@Repository
public interface ProdutoVideoRepository extends JpaRepository<ProdutoVideo, Long> {

    /**
     * Busca todos os vídeos de um produto
     */
    List<ProdutoVideo> findByProdutoId(Long produtoId);

    /**
     * Busca todos os vídeos de um produto ordenados
     */
    List<ProdutoVideo> findByProdutoIdOrderByOrdemAsc(Long produtoId);

    /**
     * Busca vídeos de um produto por tipo
     */
    List<ProdutoVideo> findByProdutoIdAndTipo(Long produtoId, TipoVideoEnum tipo);

    /**
     * Busca vídeos de um produto por tipo ordenados
     */
    List<ProdutoVideo> findByProdutoIdAndTipoOrderByOrdemAsc(Long produtoId, TipoVideoEnum tipo);

    /**
     * Busca vídeos por tipo
     */
    List<ProdutoVideo> findByTipo(TipoVideoEnum tipo);

    /**
     * Conta vídeos de um produto
     */
    Long countByProdutoId(Long produtoId);

    /**
     * Conta vídeos de um produto por tipo
     */
    Long countByProdutoIdAndTipo(Long produtoId, TipoVideoEnum tipo);

    /**
     * Deleta todos os vídeos de um produto
     */
    void deleteByProdutoId(Long produtoId);
}
