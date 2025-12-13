package com.energygames.lojadegames.dto.response;

import java.math.BigDecimal;

public record ItemPedidoResponseDTO(
    Long produtoId,
    String produtoNome,
    Integer quantidade,
    BigDecimal precoUnitario,
    BigDecimal descontoUnitario,
    BigDecimal subtotal
) {}
