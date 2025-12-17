package com.energygames.lojadegames.dto.response;

import java.math.BigDecimal;

public record PaymentStatusDTO(
    Long pedidoId,
    String status,  // "pending" | "paid" | "failed" | "cancelled"
    BigDecimal valorTotal
) {}
