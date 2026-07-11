package com.moddynerd.transiq.payment.processor;

import com.moddynerd.transiq.payment.attempt.entity.PaymentAttempt;
import com.moddynerd.transiq.payment.attempt.service.PaymentAttemptService;
import com.moddynerd.transiq.payment.authorization.AuthorizationDecision;
import com.moddynerd.transiq.payment.authorization.AuthorizationEngine;
import com.moddynerd.transiq.payment.authorization.AuthorizationResult;
import com.moddynerd.transiq.payment.entity.Payment;
import com.moddynerd.transiq.payment.entity.PaymentStatus;
import com.moddynerd.transiq.payment.financialEvent.entity.FinancialEvent;
import com.moddynerd.transiq.payment.financialEvent.entity.FinancialEventType;
import com.moddynerd.transiq.payment.financialEvent.service.FinancialEventService;
import com.moddynerd.transiq.payment.ledger.service.LedgerService;
import com.moddynerd.transiq.payment.repository.PaymentRepository;
import com.moddynerd.transiq.payment.state.PaymentStateMachine;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentProcessorImpl implements PaymentProcessor {

    private final PaymentAttemptService paymentAttemptService;
    private final PaymentStateMachine paymentStateMachine;
    private final PaymentRepository paymentRepository;
    private final AuthorizationEngine authorizationEngine;
    private final LedgerService ledgerService;
    private final FinancialEventService financialEventService;

    @Override
    public void process(Payment payment) {

        PaymentAttempt attempt =
                paymentAttemptService.createAttempt(payment);

        paymentAttemptService.markProcessing(attempt);

        paymentStateMachine.transition(
                payment,
                PaymentStatus.PROCESSING
        );

        paymentRepository.save(payment);

        AuthorizationResult result =
                authorizationEngine.authorize(payment);

        if (result.decision() == AuthorizationDecision.APPROVED) {

            paymentAttemptService.markSucceeded(attempt);

            paymentStateMachine.transition(
                    payment,
                    PaymentStatus.SUCCEEDED
            );

            FinancialEvent event =
                    financialEventService.create(
                            FinancialEventType.PAYMENT,
                            payment.getPaymentReference(),
                            "Payment completed"
                    );

            ledgerService.recordSuccessfulPayment(
                    event,
                    payment
            );

        } else {

            paymentAttemptService.markFailed(
                    attempt,
                    result.failureCode(),
                    result.message()
            );

            paymentStateMachine.transition(
                    payment,
                    PaymentStatus.FAILED
            );
        }

        paymentRepository.save(payment);
    }
}