package com.moddynerd.transiq.payment.ledger.service;

import com.moddynerd.transiq.payment.entity.Payment;
import com.moddynerd.transiq.payment.financialEvent.entity.FinancialEvent;
import com.moddynerd.transiq.payment.refund.entity.Refund;
import com.moddynerd.transiq.payment.settlement.entity.Settlement;

public interface LedgerService {

    void recordSuccessfulPayment(
            FinancialEvent event,
            Payment payment
    );

    void recordSettlement(
            FinancialEvent event,
            Settlement settlement
    );

    void recordRefund(
            FinancialEvent event,
            Refund refund
    );

}