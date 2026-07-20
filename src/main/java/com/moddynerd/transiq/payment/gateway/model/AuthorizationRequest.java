package com.moddynerd.transiq.payment.gateway.model;

import com.moddynerd.transiq.payment.entity.Currency;
import com.moddynerd.transiq.payment.entity.PaymentMethodType;
import com.moddynerd.transiq.payment.gateway.card.Card;
import com.moddynerd.transiq.payment.gateway.metadata.CardMetadata;

import java.time.Instant;
import java.util.UUID;

public record AuthorizationRequest(
        UUID paymentId,
        String paymentReference,
        UUID merchantId,
        Long amount,
        Currency currency,
        PaymentMethodType paymentMethodType,
        Card card,
        CardMetadata metadata,
        Instant requestedAt
) {}
