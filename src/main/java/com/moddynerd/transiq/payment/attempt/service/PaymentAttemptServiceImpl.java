package com.moddynerd.transiq.payment.attempt.service;

import com.moddynerd.transiq.payment.attempt.entity.AttemptStatus;
import com.moddynerd.transiq.payment.attempt.entity.FailureCode;
import com.moddynerd.transiq.payment.attempt.entity.PaymentAttempt;
import com.moddynerd.transiq.payment.attempt.repository.PaymentAttemptRepository;
import com.moddynerd.transiq.payment.entity.Payment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentAttemptServiceImpl
        implements PaymentAttemptService {

    private final PaymentAttemptRepository paymentAttemptRepository;

    @Override
    public PaymentAttempt createAttempt(Payment payment) {

        long count = paymentAttemptRepository.countByPayment(payment);

        PaymentAttempt attempt = PaymentAttempt.builder()
                .payment(payment)
                .attemptNumber((int) count + 1)
                .status(AttemptStatus.CREATED)
                .failureCode(FailureCode.NONE)
                .startedAt(Instant.now())
                .build();

        return paymentAttemptRepository.save(attempt);
    }

    @Override
    public PaymentAttempt markProcessing(PaymentAttempt attempt) {

        attempt.setStatus(AttemptStatus.PROCESSING);

        return paymentAttemptRepository.save(attempt);
    }

    @Override
    public PaymentAttempt markSucceeded(PaymentAttempt attempt) {

        attempt.setStatus(AttemptStatus.SUCCEEDED);
        attempt.setCompletedAt(Instant.now());

        attempt.setProcessingTimeMs(
                attempt.getCompletedAt().toEpochMilli()
                        - attempt.getStartedAt().toEpochMilli()
        );

        return paymentAttemptRepository.save(attempt);
    }

    @Override
    public PaymentAttempt markFailed(
            PaymentAttempt attempt,
            FailureCode failureCode,
            String failureMessage
    ) {

        attempt.setStatus(AttemptStatus.FAILED);
        attempt.setFailureCode(failureCode);
        attempt.setFailureMessage(failureMessage);
        attempt.setCompletedAt(Instant.now());

        attempt.setProcessingTimeMs(
                attempt.getCompletedAt().toEpochMilli()
                        - attempt.getStartedAt().toEpochMilli()
        );

        return paymentAttemptRepository.save(attempt);
    }
}