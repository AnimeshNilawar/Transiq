package com.moddynerd.transiq.payment.ledger.repository;

import com.moddynerd.transiq.merchant.entity.Merchant;
import com.moddynerd.transiq.payment.entity.Payment;
import com.moddynerd.transiq.payment.financialEvent.entity.FinancialEvent;
import com.moddynerd.transiq.payment.ledger.entity.LedgerEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.UUID;

public interface LedgerEntryRepository
        extends JpaRepository<LedgerEntry, UUID>, JpaSpecificationExecutor<LedgerEntry> {

    List<LedgerEntry> findAllByFinancialEvent(
            FinancialEvent financialEvent
    );

    List<LedgerEntry> findAllByMerchantOrderByCreatedAtAsc(
            Merchant merchant
    );

}