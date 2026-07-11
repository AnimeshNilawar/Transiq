package com.moddynerd.transiq.event.payment;

import com.moddynerd.transiq.event.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public record PaymentSucceededEvent(
        UUID eventId,
        Instant occurredAt,
        UUID paymentId,
        String paymentReference
) implements DomainEvent {

    public PaymentSucceededEvent(UUID paymentId, String paymentReference){
        this(
                UUID.randomUUID(),
                Instant.now(),
                paymentId,
                paymentReference
        );
    }

}
