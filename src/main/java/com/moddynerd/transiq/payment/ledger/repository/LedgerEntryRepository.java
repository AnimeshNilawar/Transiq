package com.moddynerd.transiq.payment.ledger.repository;

import com.moddynerd.transiq.merchant.entity.Merchant;
import com.moddynerd.transiq.payment.entity.Payment;
import com.moddynerd.transiq.payment.ledger.entity.LedgerEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface LedgerEntryRepository
        extends JpaRepository<LedgerEntry, UUID> {

    List<LedgerEntry> findAllByPayment(Payment payment);

    List<LedgerEntry> findAllByMerchantOrderByCreatedAtAsc(
            Merchant merchant
    );

}