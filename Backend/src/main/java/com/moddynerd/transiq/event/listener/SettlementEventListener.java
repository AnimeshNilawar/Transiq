package com.moddynerd.transiq.event.listener;

import com.moddynerd.transiq.event.settlement.SettlementCompletedEvent;
import com.moddynerd.transiq.payment.financialEvent.entity.FinancialEvent;
import com.moddynerd.transiq.payment.financialEvent.entity.FinancialEventType;
import com.moddynerd.transiq.payment.financialEvent.service.FinancialEventService;
import com.moddynerd.transiq.payment.ledger.service.LedgerService;
import com.moddynerd.transiq.payment.settlement.entity.Settlement;
import com.moddynerd.transiq.payment.settlement.repository.SettlementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class SettlementEventListener {

    private final SettlementRepository settlementRepository;
    private final FinancialEventService financialEventService;
    private final LedgerService ledgerService;

    @EventListener
    @Transactional
    public void onSettlementCompleted(
            SettlementCompletedEvent event
    ) {

        Settlement settlement =
                settlementRepository.findById(event.settlementId())
                        .orElseThrow();

        FinancialEvent financialEvent =
                financialEventService.create(
                        settlement.getMerchant(),
                        FinancialEventType.SETTLEMENT,
                        settlement.getSettlementReference(),
                        "Settlement completed"
                );

        ledgerService.recordSettlement(
                financialEvent,
                settlement
        );
    }
}