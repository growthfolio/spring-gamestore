package com.energygames.lojadegames.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.energygames.lojadegames.dto.request.CheckoutRequestDTO;
import com.energygames.lojadegames.dto.response.CheckoutResponseDTO;
import com.energygames.lojadegames.dto.response.PaymentStatusDTO;
import com.energygames.lojadegames.service.BillingService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/billing")
@Tag(name = "Billing", description = "Endpoints para pagamentos via Stripe")
public class BillingController {

    private final BillingService billingService;

    public BillingController(BillingService billingService) {
        this.billingService = billingService;
    }

    @PostMapping("/checkout")
    @Operation(summary = "Criar Checkout Session", description = "Cria uma sessão de checkout no Stripe para um pedido existente")
    public ResponseEntity<CheckoutResponseDTO> createCheckoutSession(@Valid @RequestBody CheckoutRequestDTO request) {
        return ResponseEntity.ok(billingService.createCheckoutSession(request));
    }

    @GetMapping("/status/{sessionId}")
    @Operation(summary = "Verificar status do pagamento", description = "Retorna o status atual do pagamento de uma sessão")
    public ResponseEntity<PaymentStatusDTO> getPaymentStatus(@PathVariable String sessionId) {
        return ResponseEntity.ok(billingService.getPaymentStatus(sessionId));
    }
}
