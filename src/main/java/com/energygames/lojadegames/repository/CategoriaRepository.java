package com.energygames.lojadegames.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.energygames.lojadegames.model.Categoria;

import java.util.Optional;

public interface CategoriaRepository extends JpaRepository<Categoria, Long>, JpaSpecificationExecutor<Categoria> {
    
    /**
     * Busca categoria por ID da IGDB
     * @param idIgdb ID do gênero na API IGDB
     * @return Categoria encontrada
     */
    Optional<Categoria> findByIdIgdb(Integer idIgdb);
}
