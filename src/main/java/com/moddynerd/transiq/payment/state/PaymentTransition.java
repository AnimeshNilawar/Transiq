package com.moddynerd.transiq.payment.state;

import com.moddynerd.transiq.payment.entity.PaymentStatus;

import java.util.Set;

public final class PaymentTransition {

    private PaymentTransition() {
    }

    public static final Set<PaymentStatus> FROM_REQUIRES_PAYMENT_METHOD =
            Set.of(
                    PaymentStatus.PROCESSING,
                    PaymentStatus.CANCELLED,
                    PaymentStatus.EXPIRED
            );

    public static final Set<PaymentStatus> FROM_PROCESSING =
            Set.of(
                    PaymentStatus.SUCCEEDED,
                    PaymentStatus.FAILED
            );

    public static final Set<PaymentStatus> FROM_FAILED =
            Set.of(
                    PaymentStatus.REQUIRES_PAYMENT_METHOD,
                    PaymentStatus.CANCELLED
            );

    public static final Set<PaymentStatus> FROM_SUCCEEDED =
            Set.of(
                    PaymentStatus.REFUNDED
            );

    public static final Set<PaymentStatus> FROM_CANCELLED =
            Set.of();

    public static final Set<PaymentStatus> FROM_REFUNDED =
            Set.of();
}