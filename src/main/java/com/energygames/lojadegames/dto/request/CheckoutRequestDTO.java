package com.energygames.lojadegames.dto.request;

import jakarta.validation.constraints.NotNull;

public record CheckoutRequestDTO(
    @NotNull(message = "ID do pedido é obrigatório")
    Long pedidoId
) {}
