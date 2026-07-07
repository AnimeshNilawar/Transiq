package com.moddynerd.transiq.payment.state;

import com.moddynerd.transiq.payment.entity.Payment;
import com.moddynerd.transiq.payment.entity.PaymentStatus;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class PaymentStateMachine {

    public void transition(
            Payment payment,
            PaymentStatus newStatus
    ) {

        PaymentStatus currentStatus = payment.getStatus();

        Set<PaymentStatus> allowedTransitions =
                switch (currentStatus) {

                    case CREATED -> null;

                    case REQUIRES_PAYMENT_METHOD ->
                            PaymentTransition.FROM_REQUIRES_PAYMENT_METHOD;

                    case PROCESSING ->
                            PaymentTransition.FROM_PROCESSING;

                    case FAILED ->
                            PaymentTransition.FROM_FAILED;

                    case SUCCEEDED ->
                            PaymentTransition.FROM_SUCCEEDED;

                    case CANCELLED ->
                            PaymentTransition.FROM_CANCELLED;

                    case REFUNDED ->
                            PaymentTransition.FROM_REFUNDED;
                };

        if (!allowedTransitions.contains(newStatus)) {
            throw new InvalidPaymentStateException(
                    "Cannot transition payment from "
                            + currentStatus
                            + " to "
                            + newStatus
            );
        }

        payment.setStatus(newStatus);
    }
}