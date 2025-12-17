package com.energygames.lojadegames.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.energygames.lojadegames.model.Banner;

@Repository
public interface BannerRepository extends JpaRepository<Banner, Long> {

    /**
     * Busca todos os banners ativos ordenados por ordem
     */
    List<Banner> findByAtivoTrueOrderByOrdemAsc();

    /**
     * Busca banners ativos dentro do período de exibição
     */
    @Query("SELECT b FROM Banner b WHERE b.ativo = true " +
           "AND (b.dataInicio IS NULL OR b.dataInicio <= :now) " +
           "AND (b.dataFim IS NULL OR b.dataFim >= :now) " +
           "ORDER BY b.ordem ASC")
    List<Banner> findBannersAtivosNoPeriodo(@Param("now") LocalDateTime now);

    /**
     * Busca banners vinculados a um produto específico
     */
    List<Banner> findByProdutoId(Long produtoId);

    /**
     * Conta quantos banners ativos existem
     */
    long countByAtivoTrue();
}
