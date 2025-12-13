package com.energygames.lojadegames.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.energygames.lojadegames.enums.StatusPedidoEnum;

public record PedidoResponseDTO(
    Long id,
    StatusPedidoEnum status,
    BigDecimal valorTotal,
    LocalDateTime dataCriacao,
    List<ItemPedidoResponseDTO> itens
) {}
