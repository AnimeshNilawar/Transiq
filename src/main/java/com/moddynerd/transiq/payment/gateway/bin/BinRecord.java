package com.moddynerd.transiq.payment.gateway.bin;

import com.moddynerd.transiq.payment.gateway.card.CardBrand;
import com.moddynerd.transiq.payment.gateway.card.CardType;
import com.moddynerd.transiq.payment.gateway.model.BankCode;
import com.moddynerd.transiq.payment.gateway.model.CardNetwork;

public record BinRecord(
        String bin,
        BankCode issuerBank,
        CardNetwork cardNetwork,
        CardBrand cardBrand,
        CardType cardType,
        String country,
        String issuerName
) {}
