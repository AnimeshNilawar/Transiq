package com.moddynerd.transiq.payment.repository;

import com.moddynerd.transiq.payment.entity.CardPaymentDetails;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CardPaymentDetailsRepository extends JpaRepository<CardPaymentDetails, UUID> {

    Optional<CardPaymentDetails> findByPaymentId(UUID paymentId);

}
