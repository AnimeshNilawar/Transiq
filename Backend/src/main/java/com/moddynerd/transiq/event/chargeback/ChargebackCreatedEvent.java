package com.moddynerd.transiq.event.chargeback;

import com.moddynerd.transiq.event.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public record ChargebackCreatedEvent(
        UUID eventId,
        Instant occurredAt,
        UUID chargebackId,
        String chargebackReference,
        UUID merchantId
) implements DomainEvent {

    public ChargebackCreatedEvent(
            UUID merchantId,
            UUID chargebackId,
            String chargebackReference
    ) {
        this(
                UUID.randomUUID(),
                Instant.now(),
                chargebackId,
                chargebackReference,
                merchantId
        );
    }
}
