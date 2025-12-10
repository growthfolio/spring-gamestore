package com.energygames.lojadegames.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.energygames.lojadegames.model.CarrinhoItem;

public interface CarrinhoRepository extends JpaRepository<CarrinhoItem, Long> {

	List<CarrinhoItem> findAllByUsuarioId(Long usuarioId);

	Optional<CarrinhoItem> findByUsuarioIdAndProdutoId(Long usuarioId, Long produtoId);

	boolean existsByUsuarioIdAndProdutoId(Long usuarioId, Long produtoId);

	@Query("SELECT COUNT(c) FROM CarrinhoItem c WHERE c.usuario.id = :usuarioId")
	Long contarItensPorUsuario(@Param("usuarioId") Long usuarioId);

	@Query("SELECT SUM(c.quantidade) FROM CarrinhoItem c WHERE c.usuario.id = :usuarioId")
	Integer somarQuantidadePorUsuario(@Param("usuarioId") Long usuarioId);

	void deleteAllByUsuarioId(Long usuarioId);
}
