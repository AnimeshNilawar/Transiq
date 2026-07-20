package com.moddynerd.transiq.payment.attempt.service;

import com.moddynerd.transiq.payment.attempt.entity.FailureCode;
import com.moddynerd.transiq.payment.attempt.entity.PaymentAttempt;
import com.moddynerd.transiq.payment.entity.Payment;

public interface PaymentAttemptService {

    PaymentAttempt createAttempt(Payment payment);

    PaymentAttempt markProcessing(PaymentAttempt attempt);

    PaymentAttempt markSucceeded(PaymentAttempt attempt);

    PaymentAttempt markFailed(
            PaymentAttempt attempt,
            FailureCode failureCode,
            String failureMessage
    );

}