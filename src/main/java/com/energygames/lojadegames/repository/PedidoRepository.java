package com.energygames.lojadegames.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.energygames.lojadegames.model.Pedido;
import com.energygames.lojadegames.model.Usuario;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    List<Pedido> findByUsuario(Usuario usuario);
    
    Optional<Pedido> findByStripeSessionId(String stripeSessionId);
    
    List<Pedido> findByUsuarioIdOrderByDataCriacaoDesc(Long usuarioId, Pageable pageable);
    
    @Query("SELECT COUNT(DISTINCT p.id) FROM Pedido p WHERE p.usuario.id = :usuarioId")
    int contarPedidosPorUsuario(@Param("usuarioId") Long usuarioId);
    
    @Query("SELECT COALESCE(SUM(p.valorTotal), 0) FROM Pedido p WHERE p.usuario.id = :usuarioId")
    BigDecimal somarValorTotalPorUsuario(@Param("usuarioId") Long usuarioId);
    
    @Query("SELECT c.tipo, COUNT(ip) FROM Pedido p " +
           "JOIN p.itens ip " +
           "JOIN ip.produto pr " +
           "JOIN pr.categoria c " +
           "WHERE p.usuario.id = :usuarioId " +
           "GROUP BY c.tipo " +
           "ORDER BY COUNT(ip) DESC")
    List<Object[]> contarComprasPorCategoria(@Param("usuarioId") Long usuarioId);
}
