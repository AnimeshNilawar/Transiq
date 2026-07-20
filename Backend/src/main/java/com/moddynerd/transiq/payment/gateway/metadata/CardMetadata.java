package com.moddynerd.transiq.payment.gateway.metadata;

import com.moddynerd.transiq.payment.gateway.card.CardBrand;
import com.moddynerd.transiq.payment.gateway.card.CardType;
import com.moddynerd.transiq.payment.gateway.model.BankCode;
import com.moddynerd.transiq.payment.gateway.model.CardNetwork;

public record CardMetadata(
        BankCode issuerBank,
        CardNetwork network,
        CardBrand brand,
        String country,
        CardType cardType,
        String issuerName
) {}
