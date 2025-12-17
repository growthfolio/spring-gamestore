package com.energygames.lojadegames.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.energygames.lojadegames.configuration.StripeConfig;
import com.energygames.lojadegames.service.BillingService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;

import io.swagger.v3.oas.annotations.Hidden;

@RestController
@RequestMapping("/api/billing/webhook")
@Hidden // Não expor no Swagger
public class StripeWebhookController {

    private static final Logger log = LoggerFactory.getLogger(StripeWebhookController.class);

    private final BillingService billingService;
    private final StripeConfig stripeConfig;

    public StripeWebhookController(BillingService billingService, StripeConfig stripeConfig) {
        this.billingService = billingService;
        this.stripeConfig = stripeConfig;
    }

    @PostMapping
    public ResponseEntity<String> handleWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader) {
        
        Event event;
        
        try {
            event = Webhook.constructEvent(payload, sigHeader, stripeConfig.getWebhookSecret());
        } catch (SignatureVerificationException e) {
            log.error("Falha na verificação de assinatura do webhook: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Assinatura inválida");
        } catch (Exception e) {
            log.error("Erro ao processar webhook: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Payload inválido");
        }

        String eventType = event.getType();
        String eventId = event.getId();

        log.info("Webhook recebido: type={}, id={}", eventType, eventId);

        try {
            switch (eventType) {
                case "checkout.session.completed" -> {
                    Session session = (Session) event.getDataObjectDeserializer()
                            .getObject()
                            .orElseThrow(() -> new RuntimeException("Falha ao deserializar session"));
                    
                    String paymentStatus = session.getPaymentStatus();
                    
                    if ("paid".equals(paymentStatus)) {
                        billingService.handlePaymentSuccess(session.getId(), eventId);
                    } else {
                        log.info("Checkout completed mas pagamento não confirmado: {}", paymentStatus);
                    }
                }
                
                case "checkout.session.expired" -> {
                    Session session = (Session) event.getDataObjectDeserializer()
                            .getObject()
                            .orElseThrow(() -> new RuntimeException("Falha ao deserializar session"));
                    
                    billingService.handlePaymentFailure(session.getId(), eventId);
                }
                
                case "payment_intent.succeeded" -> {
                    log.info("PaymentIntent succeeded - processado via checkout.session.completed");
                }
                
                case "payment_intent.payment_failed" -> {
                    log.warn("PaymentIntent falhou: {}", eventId);
                }
                
                default -> log.info("Evento não tratado: {}", eventType);
            }
        } catch (Exception e) {
            log.error("Erro ao processar evento {}: {}", eventType, e.getMessage(), e);
            // Retornar 200 mesmo com erro para evitar retry infinito
            // O erro será tratado manualmente via logs
            return ResponseEntity.ok("Evento recebido com erro no processamento");
        }

        return ResponseEntity.ok("Webhook processado");
    }
}
