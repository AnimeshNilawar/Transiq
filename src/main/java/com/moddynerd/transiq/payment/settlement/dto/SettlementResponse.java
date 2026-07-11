package com.moddynerd.transiq.payment.settlement.dto;

import java.time.Instant;

public record SettlementResponse(

        String settlementReference,

        Long amount,

        String currency,

        String status,

        Instant processedAt,

        String bankReference

) {
}