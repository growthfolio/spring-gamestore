package com.energygames.lojadegames.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.energygames.lojadegames.enums.OrigemEnum;
import com.energygames.lojadegames.model.ProdutoOrigemExterna;

/**
 * Repository para operações de banco de dados da entidade ProdutoOrigemExterna.
 */
@Repository
public interface ProdutoOrigemExternaRepository extends JpaRepository<ProdutoOrigemExterna, Long> {

    /**
     * Busca origem externa pelo ID do produto
     */
    Optional<ProdutoOrigemExterna> findByProdutoId(Long produtoId);

    /**
     * Busca origem externa pela origem e ID externo
     */
    Optional<ProdutoOrigemExterna> findByOrigemAndIdExterno(OrigemEnum origem, String idExterno);

    /**
     * Busca todos os produtos de uma origem específica
     */
    List<ProdutoOrigemExterna> findByOrigem(OrigemEnum origem);

    /**
     * Busca produtos com sincronização ativa de uma origem específica
     */
    List<ProdutoOrigemExterna> findByOrigemAndSincronizacaoAtivaTrue(OrigemEnum origem);

    /**
     * Busca produtos que precisam ser sincronizados (não foram sincronizados recentemente)
     */
    @Query("SELECT poe FROM ProdutoOrigemExterna poe WHERE poe.origem = :origem " +
           "AND poe.sincronizacaoAtiva = true " +
           "AND (poe.dataUltimaSincronizacao IS NULL OR poe.dataUltimaSincronizacao < :dataMinimaSync)")
    List<ProdutoOrigemExterna> findProdutosParaSincronizar(
        @Param("origem") OrigemEnum origem,
        @Param("dataMinimaSync") LocalDateTime dataMinimaSync
    );

    /**
     * Conta produtos por origem
     */
    Long countByOrigem(OrigemEnum origem);

    /**
     * Conta produtos com sincronização ativa por origem
     */
    Long countByOrigemAndSincronizacaoAtivaTrue(OrigemEnum origem);

    /**
     * Verifica se existe um produto com aquele ID externo
     */
    boolean existsByOrigemAndIdExterno(OrigemEnum origem, String idExterno);
}
