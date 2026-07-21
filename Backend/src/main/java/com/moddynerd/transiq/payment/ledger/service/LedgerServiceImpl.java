package com.moddynerd.transiq.payment.ledger.service;

import com.moddynerd.transiq.merchant.entity.Merchant;
import com.moddynerd.transiq.payment.entity.Payment;
import com.moddynerd.transiq.payment.financialEvent.entity.FinancialEvent;
import com.moddynerd.transiq.payment.ledger.entity.EntrySide;
import com.moddynerd.transiq.payment.ledger.entity.LedgerAccount;
import com.moddynerd.transiq.payment.ledger.entity.LedgerEntry;
import com.moddynerd.transiq.payment.ledger.entity.LedgerEntryType;
import com.moddynerd.transiq.payment.ledger.repository.LedgerEntryRepository;
import com.moddynerd.transiq.payment.refund.entity.Refund;
import com.moddynerd.transiq.payment.settlement.entity.Settlement;
import com.moddynerd.transiq.payment.settlement.repository.SettlementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;



@Service
@RequiredArgsConstructor
@Transactional
public class LedgerServiceImpl implements LedgerService {

    private final LedgerEntryRepository ledgerEntryRepository;
    private final SettlementRepository settlementRepository;

    @Override
    public void recordSuccessfulPayment(
            FinancialEvent event,
            Payment payment
    ) {

        Long amount = payment.getAmount();
        Long platformFee = (long) (amount * 0.02);
        Long merchantAmount = amount - platformFee;

        createEntry(
                event,
                LedgerEntryType.PAYMENT,
                LedgerAccount.CUSTOMER_RECEIVABLE,
                EntrySide.DEBIT,
                amount,
                "Customer payment received"
        );

        createEntry(
                event,
                LedgerEntryType.PAYMENT,
                LedgerAccount.MERCHANT_PAYABLE,
                EntrySide.CREDIT,
                merchantAmount,
                "Amount payable to merchant (net of platform fee)"
        );

        createEntry(
                event,
                LedgerEntryType.PAYMENT,
                LedgerAccount.PLATFORM_REVENUE,
                EntrySide.CREDIT,
                platformFee,
                "Platform fee (2%)"
        );
    }

    private void createEntry(
            FinancialEvent event,
            LedgerEntryType entryType,
            LedgerAccount account,
            EntrySide side,
            Long amount,
            String description
    ) {

        LedgerEntry entry = LedgerEntry.builder()
                .merchant(event.getMerchant())
                .financialEvent(event)
                .entryType(entryType)
                .account(account)
                .side(side)
                .amount(amount)
                .description(description)
                .build();

        ledgerEntryRepository.save(entry);
    }

    @Override
    public void recordSettlement(
            FinancialEvent event,
            Settlement settlement
    ) {

        long amount = settlement.getAmount();

        createEntry(
                event,
                LedgerEntryType.SETTLEMENT,
                LedgerAccount.MERCHANT_PAYABLE,
                EntrySide.DEBIT,
                amount,
                "Settlement to merchant"
        );

        createEntry(
                event,
                LedgerEntryType.SETTLEMENT,
                LedgerAccount.SETTLEMENT_ACCOUNT,
                EntrySide.CREDIT,
                amount,
                "Funds transferred to merchant"
        );
    }

    @Override
    public void recordRefund(
            FinancialEvent event,
            Refund refund
    ) {

        Long amount = refund.getAmount();

        createEntry(
                event,
                LedgerEntryType.REFUND,
                LedgerAccount.MERCHANT_PAYABLE,
                EntrySide.DEBIT,
                amount,
                "Refund issued"
        );

        createEntry(
                event,
                LedgerEntryType.REFUND,
                LedgerAccount.CUSTOMER_RECEIVABLE,
                EntrySide.CREDIT,
                amount,
                "Refund to customer"
        );
    }
}
