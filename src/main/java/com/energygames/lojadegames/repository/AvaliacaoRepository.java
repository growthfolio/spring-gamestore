package com.energygames.lojadegames.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.energygames.lojadegames.model.Avaliacao;

public interface AvaliacaoRepository extends JpaRepository<Avaliacao, Long> {

	Page<Avaliacao> findAllByProdutoId(Long produtoId, Pageable pageable);

	Optional<Avaliacao> findByProdutoIdAndUsuarioId(Long produtoId, Long usuarioId);

	@Query("SELECT AVG(a.nota) FROM Avaliacao a WHERE a.produto.id = :produtoId")
	Double calcularMediaPorProduto(@Param("produtoId") Long produtoId);

	@Query("SELECT COUNT(a) FROM Avaliacao a WHERE a.produto.id = :produtoId")
	Long contarAvaliacoesPorProduto(@Param("produtoId") Long produtoId);

	List<Avaliacao> findTop5ByProdutoIdOrderByDataAvaliacaoDesc(Long produtoId);
}
