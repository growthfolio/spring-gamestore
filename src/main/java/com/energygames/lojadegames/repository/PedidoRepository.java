package com.energygames.lojadegames.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.energygames.lojadegames.model.Pedido;
import com.energygames.lojadegames.model.Usuario;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    List<Pedido> findByUsuario(Usuario usuario);
}
