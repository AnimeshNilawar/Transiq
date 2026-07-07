package com.moddynerd.transiq.payment.processor;

import com.moddynerd.transiq.payment.attempt.entity.PaymentAttempt;
import com.moddynerd.transiq.payment.attempt.service.PaymentAttemptService;
import com.moddynerd.transiq.payment.entity.Payment;
import com.moddynerd.transiq.payment.entity.PaymentStatus;
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

        /*
         * For now, we simulate success.
         * Later this block will contain:
         *
         * Risk Engine
         * Authorization Engine
         * Fraud Checks
         * Bank Communication
         */

        paymentAttemptService.markSucceeded(attempt);

        paymentStateMachine.transition(
                payment,
                PaymentStatus.SUCCEEDED
        );

        paymentRepository.save(payment);
    }
}