//package com.moddynerd.transiq.event.listener;
//
//import com.moddynerd.transiq.event.payment.PaymentSucceededEvent;
//import com.moddynerd.transiq.event.refund.RefundSucceededEvent;
//import com.moddynerd.transiq.event.settlement.SettlementCompletedEvent;
//import com.moddynerd.transiq.payment.entity.Payment;
//import com.moddynerd.transiq.payment.financialEvent.entity.FinancialEvent;
//import com.moddynerd.transiq.payment.financialEvent.entity.FinancialEventType;
//import com.moddynerd.transiq.payment.financialEvent.service.FinancialEventService;
//import com.moddynerd.transiq.payment.ledger.service.LedgerService;
//import com.moddynerd.transiq.payment.refund.entity.Refund;
//import com.moddynerd.transiq.payment.refund.repository.RefundRepository;
//import com.moddynerd.transiq.payment.repository.PaymentRepository;
//import com.moddynerd.transiq.payment.settlement.entity.Settlement;
//import com.moddynerd.transiq.payment.settlement.repository.SettlementRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.context.event.EventListener;
//import org.springframework.stereotype.Component;
//import org.springframework.transaction.annotation.Transactional;
//
//@Component
//@RequiredArgsConstructor
//public class LedgerEventListener {
//
//    private final PaymentRepository paymentRepository;
//    private final RefundRepository refundRepository;
//    private final SettlementRepository settlementRepository;
//    private final FinancialEventService financialEventService;
//    private final LedgerService ledgerService;
//
//    @EventListener
//    @Transactional
//    public void onPaymentSucceeded(
//            PaymentSucceededEvent event
//    ) {
//
//        Payment payment = paymentRepository
//                .findById(event.paymentId())
//                .orElseThrow();
//
//        FinancialEvent financialEvent =
//                financialEventService.create(
//                        FinancialEventType.PAYMENT,
//                        payment.getPaymentReference(),
//                        "Payment completed"
//                );
//
//        ledgerService.recordSuccessfulPayment(
//                financialEvent,
//                payment
//        );
//    }
//
//    @EventListener
//    @Transactional
//    public void onRefundSucceeded(
//            RefundSucceededEvent event
//    ) {
//
//        Refund refund =
//                refundRepository
//                        .findById(event.refundId())
//                        .orElseThrow();
//
//        FinancialEvent financialEvent =
//                financialEventService.create(
//                        FinancialEventType.REFUND,
//                        refund.getRefundReference(),
//                        "Refund issued"
//                );
//
//        ledgerService.recordRefund(
//                financialEvent,
//                refund
//        );
//    }
//
//    @EventListener
//    @Transactional
//    public void onSettlementCompleted(
//            SettlementCompletedEvent event
//    ) {
//
//        Settlement settlement =
//                settlementRepository
//                        .findById(event.settlementId())
//                        .orElseThrow();
//
//        FinancialEvent financialEvent =
//                financialEventService.create(
//                        FinancialEventType.SETTLEMENT,
//                        settlement.getSettlementReference(),
//                        "Settlement completed"
//                );
//
//        ledgerService.recordSettlement(
//                financialEvent,
//                settlement
//        );
//    }
//}