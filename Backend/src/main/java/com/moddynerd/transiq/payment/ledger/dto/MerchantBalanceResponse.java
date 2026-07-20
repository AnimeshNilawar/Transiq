package com.moddynerd.transiq.payment.ledger.dto;

public record MerchantBalanceResponse(

        Long availableBalance,

        String currency

) {
}