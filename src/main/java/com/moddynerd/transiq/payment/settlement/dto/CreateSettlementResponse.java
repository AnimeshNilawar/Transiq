package com.moddynerd.transiq.payment.settlement.dto;

import java.time.Instant;

public record CreateSettlementResponse(

        String settlementReference,

        Long amount,

        String currency,

        String status,

        Instant createdAt

) {
}