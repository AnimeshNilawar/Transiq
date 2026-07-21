package com.moddynerd.transiq.dashboard.dto;

import com.moddynerd.transiq.payment.entity.Currency;
import com.moddynerd.transiq.payment.entity.PaymentMethodType;
import com.moddynerd.transiq.payment.entity.PaymentStatus;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record DashboardPaymentDetailResponse(

        UUID id,

        String paymentReference,

        Long amount,

        Currency currency,

        PaymentStatus status,

        PaymentMethodType paymentMethodType,

        String customerEmail,

        String customerName,

        String orderId,

        String description,

        Long refundedAmount,

        Instant createdAt,

        DashboardCardDetailsInfo cardDetails,

        DashboardUpiDetailsInfo upiDetails,

        List<DashboardPaymentAttemptInfo> attempts

) {

    public record DashboardCardDetailsInfo(

            String cardNetwork,

            String issuerBank,

            String maskedCardNumber,

            Integer expiryMonth,

            Integer expiryYear,

            String authorizationCode,

            String gatewayResponseCode,

            String gatewayMessage

    ) {}

    public record DashboardUpiDetailsInfo(

            String upiId,

            String upiTransactionReference

    ) {}

    public record DashboardPaymentAttemptInfo(

            Integer attemptNumber,

            String status,

            String failureCode,

            String failureMessage,

            Instant startedAt,

            Instant completedAt,

            Long processingTimeMs

    ) {}
}
