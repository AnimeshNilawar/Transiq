package com.moddynerd.transiq.payment.ledger.dto;

import java.math.BigDecimal;

public record MerchantBalanceResponse(

        BigDecimal availableBalance,

        String currency

) {
}