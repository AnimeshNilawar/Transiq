package com.moddynerd.transiq.payment.gateway.bank.configuration;

import com.moddynerd.transiq.payment.gateway.model.BankCode;
import com.moddynerd.transiq.payment.gateway.model.BankType;
import com.moddynerd.transiq.payment.gateway.model.CardNetwork;

import java.util.Set;

public record BankConfiguration(
        BankCode bankCode,
        double approvalRate,
        double declineRate,
        Long maxTransactionAmount,
        Set<CardNetwork> supportedNetworks,
        BankType bankType
) {}
