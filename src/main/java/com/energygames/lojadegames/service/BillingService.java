package com.energygames.lojadegames.service;

import com.energygames.lojadegames.dto.request.CheckoutRequestDTO;
import com.energygames.lojadegames.dto.response.CheckoutResponseDTO;
import com.energygames.lojadegames.dto.response.PaymentStatusDTO;

public interface BillingService {
    
    CheckoutResponseDTO createCheckoutSession(CheckoutRequestDTO request);
    
    PaymentStatusDTO getPaymentStatus(String sessionId);
    
    void handlePaymentSuccess(String sessionId, String stripeEventId);
    
    void handlePaymentFailure(String sessionId, String stripeEventId);
}
