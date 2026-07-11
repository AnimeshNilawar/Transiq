package com.moddynerd.transiq.event.refund;

import com.moddynerd.transiq.event.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public record RefundSucceededEvent(

        UUID eventId,
        Instant occurredAt,
        UUID refundId,
        String refundReference

) implements DomainEvent {

    public RefundSucceededEvent(
            UUID refundId,
            String refundReference
    ) {
        this(
                UUID.randomUUID(),
                Instant.now(),
                refundId,
                refundReference
        );
    }
}