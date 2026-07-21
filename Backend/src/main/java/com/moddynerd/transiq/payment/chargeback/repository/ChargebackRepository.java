package com.moddynerd.transiq.payment.chargeback.repository;

import com.moddynerd.transiq.merchant.entity.Merchant;
import com.moddynerd.transiq.payment.chargeback.entity.Chargeback;
import com.moddynerd.transiq.payment.chargeback.entity.ChargebackStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChargebackRepository extends JpaRepository<Chargeback, UUID> {

    Optional<Chargeback> findByChargebackReference(String chargebackReference);

    List<Chargeback> findAllByMerchantOrderByCreatedAtDesc(Merchant merchant);

    long countByMerchantAndStatusIn(Merchant merchant, List<ChargebackStatus> statuses);
}
