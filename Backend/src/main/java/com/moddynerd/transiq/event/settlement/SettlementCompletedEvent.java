package com.moddynerd.transiq.event.settlement;

import com.moddynerd.transiq.event.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public record SettlementCompletedEvent(

        UUID eventId,
        Instant occurredAt,
        UUID settlementId,
        String settlementReference,
        UUID merchantId

) implements DomainEvent {

    public SettlementCompletedEvent(
            UUID merchantId,
            UUID settlementId,
            String settlementReference
    ) {
        this(
                UUID.randomUUID(),
                Instant.now(),
                settlementId,
                settlementReference,
                merchantId
        );
    }
}
