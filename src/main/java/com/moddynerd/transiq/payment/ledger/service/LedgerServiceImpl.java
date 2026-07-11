package com.moddynerd.transiq.payment.ledger.service;

import com.moddynerd.transiq.payment.entity.Payment;
import com.moddynerd.transiq.payment.ledger.entity.EntrySide;
import com.moddynerd.transiq.payment.ledger.entity.LedgerAccount;
import com.moddynerd.transiq.payment.ledger.entity.LedgerEntry;
import com.moddynerd.transiq.payment.ledger.entity.LedgerEntryType;
import com.moddynerd.transiq.payment.ledger.repository.LedgerEntryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Transactional
public class LedgerServiceImpl implements LedgerService {

    private final LedgerEntryRepository ledgerEntryRepository;

    @Override
    public void recordSuccessfulPayment(Payment payment) {
        BigDecimal amount = BigDecimal.valueOf(payment.getAmount());

        createEntry(
                payment,
                LedgerAccount.CUSTOMER_RECEIVABLE,
                EntrySide.DEBIT,
                amount,
                "Customer payment received"
        );

        createEntry(
                payment,
                LedgerAccount.MERCHANT_PAYABLE,
                EntrySide.CREDIT,
                amount,
                "Amount payable to merchant"
        );
    }

    private void createEntry(
            Payment payment,
            LedgerAccount account,
            EntrySide side,
            BigDecimal amount,
            String description
    ) {
        LedgerEntry entry = LedgerEntry.builder()
                .payment(payment)
                .merchant(payment.getMerchant())
                .entryType(LedgerEntryType.PAYMENT)
                .account(account)
                .side(side)
                .amount(amount)
                .description(description)
                .build();

        ledgerEntryRepository.save(entry);
    }
}
