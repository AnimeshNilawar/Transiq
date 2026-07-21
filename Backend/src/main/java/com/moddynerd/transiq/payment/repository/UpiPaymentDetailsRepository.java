package com.moddynerd.transiq.payment.repository;

import com.moddynerd.transiq.payment.entity.UpiPaymentDetails;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UpiPaymentDetailsRepository extends JpaRepository<UpiPaymentDetails, UUID> {

    Optional<UpiPaymentDetails> findByPaymentId(UUID paymentId);
}
