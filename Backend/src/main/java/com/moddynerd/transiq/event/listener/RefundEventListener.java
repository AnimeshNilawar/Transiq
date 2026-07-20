package com.moddynerd.transiq.event.listener;

import com.moddynerd.transiq.event.refund.RefundSucceededEvent;
import com.moddynerd.transiq.payment.financialEvent.entity.FinancialEvent;
import com.moddynerd.transiq.payment.financialEvent.entity.FinancialEventType;
import com.moddynerd.transiq.payment.financialEvent.service.FinancialEventService;
import com.moddynerd.transiq.payment.ledger.service.LedgerService;
import com.moddynerd.transiq.payment.refund.entity.Refund;
import com.moddynerd.transiq.payment.refund.repository.RefundRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class RefundEventListener {

    private final RefundRepository refundRepository;
    private final FinancialEventService financialEventService;
    private final LedgerService ledgerService;

    @EventListener
    @Transactional
    public void onRefundSucceeded(
            RefundSucceededEvent event
    ) {

        Refund refund =
                refundRepository.findById(event.refundId())
                        .orElseThrow();

        FinancialEvent financialEvent =
                financialEventService.create(
                        FinancialEventType.REFUND,
                        refund.getRefundReference(),
                        "Refund completed"
                );

        ledgerService.recordRefund(
                financialEvent,
                refund
        );
    }
}