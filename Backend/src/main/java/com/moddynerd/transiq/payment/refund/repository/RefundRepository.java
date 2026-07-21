package com.moddynerd.transiq.payment.refund.repository;

import com.moddynerd.transiq.merchant.entity.Merchant;
import com.moddynerd.transiq.payment.entity.Payment;
import com.moddynerd.transiq.payment.refund.entity.Refund;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RefundRepository
        extends JpaRepository<Refund, UUID>, JpaSpecificationExecutor<Refund> {

    List<Refund> findAllByPayment(Payment payment);

    Optional<Refund> findByRefundReference(
            String refundReference
    );

    List<Refund> findAllByMerchantOrderByCreatedAtDesc(
            Merchant merchant
    );

    Optional<Refund> findByPaymentAndIdempotencyKey(
            Payment payment,
            String idempotencyKey
    );

    Page<Refund> findAllByOrderByCreatedAtDesc(Pageable pageable);
}