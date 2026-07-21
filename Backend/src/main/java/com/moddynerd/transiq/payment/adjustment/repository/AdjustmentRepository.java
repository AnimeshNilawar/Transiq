package com.moddynerd.transiq.payment.adjustment.repository;

import com.moddynerd.transiq.merchant.entity.Merchant;
import com.moddynerd.transiq.payment.adjustment.entity.Adjustment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AdjustmentRepository extends JpaRepository<Adjustment, UUID> {

    Optional<Adjustment> findByAdjustmentReference(String adjustmentReference);

    List<Adjustment> findAllByMerchantOrderByCreatedAtDesc(Merchant merchant);
}
