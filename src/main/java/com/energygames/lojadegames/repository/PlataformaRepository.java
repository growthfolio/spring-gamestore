package com.energygames.lojadegames.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.energygames.lojadegames.enums.TipoPlataformaEnum;
import com.energygames.lojadegames.model.Plataforma;

/**
 * Repository para operações de banco de dados da entidade Plataforma.
 */
@Repository
public interface PlataformaRepository extends JpaRepository<Plataforma, Long> {

    /**
     * Busca uma plataforma pelo slug
     */
    Optional<Plataforma> findBySlug(String slug);

    /**
     * Busca uma plataforma pelo ID da IGDB
     */
    Optional<Plataforma> findByIdIgdb(Integer idIgdb);

    /**
     * Busca plataformas ativas
     */
    List<Plataforma> findByAtivaTrue();

    /**
     * Busca plataformas por tipo
     */
    List<Plataforma> findByTipo(TipoPlataformaEnum tipo);

    /**
     * Busca plataformas ativas por tipo
     */
    List<Plataforma> findByAtivaAndTipo(Boolean ativa, TipoPlataformaEnum tipo);

    /**
     * Busca plataformas por geração
     */
    List<Plataforma> findByGeracao(Integer geracao);

    /**
     * Verifica se existe uma plataforma com o nome especificado
     */
    boolean existsByNome(String nome);

    /**
     * Verifica se existe uma plataforma com o slug especificado
     */
    boolean existsBySlug(String slug);

    /**
     * Conta o número de produtos associados a uma plataforma
     */
    @Query("SELECT COUNT(p) FROM Produto p JOIN p.plataformas plat WHERE plat.id = :plataformaId")
    Long contarProdutosPorPlataforma(@Param("plataformaId") Long plataformaId);
}
