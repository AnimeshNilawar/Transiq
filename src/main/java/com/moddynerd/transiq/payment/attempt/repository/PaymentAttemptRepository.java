package com.moddynerd.transiq.payment.attempt.repository;

import com.moddynerd.transiq.payment.attempt.entity.PaymentAttempt;
import com.moddynerd.transiq.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PaymentAttemptRepository
        extends JpaRepository<PaymentAttempt, UUID> {

    List<PaymentAttempt> findAllByPaymentOrderByAttemptNumberDesc(
            Payment payment
    );

    long countByPayment(Payment payment);

}