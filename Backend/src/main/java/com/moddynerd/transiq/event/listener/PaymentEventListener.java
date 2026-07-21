package com.moddynerd.transiq.event.listener;

import com.moddynerd.transiq.event.payment.PaymentSucceededEvent;
import com.moddynerd.transiq.payment.entity.Payment;
import com.moddynerd.transiq.payment.financialEvent.entity.FinancialEvent;
import com.moddynerd.transiq.payment.financialEvent.entity.FinancialEventType;
import com.moddynerd.transiq.payment.financialEvent.service.FinancialEventService;
import com.moddynerd.transiq.payment.ledger.service.LedgerService;
import com.moddynerd.transiq.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class PaymentEventListener {

    private final PaymentRepository paymentRepository;
    private final FinancialEventService financialEventService;
    private final LedgerService ledgerService;

    @EventListener
    @Transactional
    public void onPaymentSucceeded(
            PaymentSucceededEvent event
    ) {

        Payment payment =
                paymentRepository.findById(event.paymentId())
                        .orElseThrow();

        FinancialEvent financialEvent =
                financialEventService.create(
                        payment.getMerchant(),
                        FinancialEventType.PAYMENT,
                        payment.getPaymentReference(),
                        "Payment completed"
                );

        ledgerService.recordSuccessfulPayment(
                financialEvent,
                payment
        );
    }
}