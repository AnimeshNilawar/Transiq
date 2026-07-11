package com.moddynerd.transiq.payment.service;

import com.moddynerd.transiq.payment.dto.ConfirmPaymentRequest;
import com.moddynerd.transiq.payment.dto.CreatePaymentRequest;
import com.moddynerd.transiq.payment.dto.CreatePaymentResponse;
import com.moddynerd.transiq.payment.dto.PaymentResponse;

public interface PaymentService {

    CreatePaymentResponse createPayment(
            String idempotencyKey,
            CreatePaymentRequest request
    );

    PaymentResponse getPayment(String paymentReference);

    PaymentResponse confirmPayment(
            String paymentReference,
            ConfirmPaymentRequest request
    );

    PaymentResponse retryPayment(String paymentReference);
}