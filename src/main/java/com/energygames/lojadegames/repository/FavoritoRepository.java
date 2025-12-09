package com.energygames.lojadegames.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.energygames.lojadegames.model.Favorito;

public interface FavoritoRepository extends JpaRepository<Favorito, Long> {

	Page<Favorito> findAllByUsuarioId(Long usuarioId, Pageable pageable);

	Optional<Favorito> findByUsuarioIdAndProdutoId(Long usuarioId, Long produtoId);

	boolean existsByUsuarioIdAndProdutoId(Long usuarioId, Long produtoId);

	@Query("SELECT COUNT(f) FROM Favorito f WHERE f.usuario.id = :usuarioId")
	Long contarFavoritosPorUsuario(@Param("usuarioId") Long usuarioId);

	void deleteByUsuarioIdAndProdutoId(Long usuarioId, Long produtoId);
}
