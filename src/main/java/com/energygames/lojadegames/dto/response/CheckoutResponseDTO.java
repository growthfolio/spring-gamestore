package com.energygames.lojadegames.dto.response;

public record CheckoutResponseDTO(
    String checkoutUrl,
    String sessionId
) {}
