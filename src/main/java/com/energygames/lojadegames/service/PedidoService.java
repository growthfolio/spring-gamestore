package com.energygames.lojadegames.service;

import java.util.List;

import com.energygames.lojadegames.dto.response.PedidoResponseDTO;

public interface PedidoService {
    PedidoResponseDTO criarPedido();
    List<PedidoResponseDTO> listarMeusPedidos();
    PedidoResponseDTO buscarPedidoPorId(Long id);
}
