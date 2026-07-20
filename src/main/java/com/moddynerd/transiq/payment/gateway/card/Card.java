package com.moddynerd.transiq.payment.gateway.card;

import java.time.Instant;

public record Card(
        String token,
        String maskedPan,
        String bin,
        String last4,
        Instant expiry,
        CardBrand brand,
        CardType type,
        String country
) {

    public Card {
        if (bin == null || bin.isBlank()) {
            throw new IllegalArgumentException("BIN must not be blank");
        }
        if (last4 == null || last4.isBlank()) {
            throw new IllegalArgumentException("Last4 must not be blank");
        }
    }

    public String toDisplayString() {
        return "**** **** **** " + last4;
    }

}
