package com.energygames.lojadegames.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.energygames.lojadegames.model.PaymentEvent;

@Repository
public interface PaymentEventRepository extends JpaRepository<PaymentEvent, Long> {
    
    boolean existsByStripeEventId(String stripeEventId);
}
