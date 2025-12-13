package com.energygames.lojadegames.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.query.Param;

import com.energygames.lojadegames.enums.OrigemEnum;
import com.energygames.lojadegames.model.Produto;

public interface ProdutoRepository extends JpaRepository<Produto, Long>, JpaSpecificationExecutor<Produto> {

	public List<Produto> findAllByNomeContainingIgnoreCase(@Param("nome") String nome);

	Page<Produto> findByAtivoFalseAndOrigemExternaOrigem(OrigemEnum origem, Pageable pageable);
}
