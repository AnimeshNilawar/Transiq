package com.moddynerd.transiq.payment.settlement.repository;

import com.moddynerd.transiq.merchant.entity.Merchant;
import com.moddynerd.transiq.payment.settlement.entity.Settlement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SettlementRepository
        extends JpaRepository<Settlement, UUID>, JpaSpecificationExecutor<Settlement> {

    Optional<Settlement> findBySettlementReference(
            String settlementReference
    );

    List<Settlement> findAllByMerchantOrderByCreatedAtDesc(
            Merchant merchant
    );
}