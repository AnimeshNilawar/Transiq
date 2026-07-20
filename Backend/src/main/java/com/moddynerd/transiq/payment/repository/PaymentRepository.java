package com.moddynerd.transiq.payment.repository;

import com.moddynerd.transiq.merchant.entity.Merchant;
import com.moddynerd.transiq.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID>, JpaSpecificationExecutor<Payment> {

    Optional<Payment> findByPaymentReference(String paymentReference);

    Optional<Payment> findByMerchantAndIdempotencyKey(
            Merchant merchant,
            String idempotencyKey
    );

    Optional<Payment> findByMerchantAndPaymentReference(
            Merchant merchant,
            String paymentReference
    );
}