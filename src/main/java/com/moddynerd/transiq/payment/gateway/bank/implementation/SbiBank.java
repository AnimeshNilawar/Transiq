package com.moddynerd.transiq.payment.gateway.bank.implementation;

import com.moddynerd.transiq.payment.gateway.bank.configuration.BankConfiguration;
import com.moddynerd.transiq.payment.gateway.bank.simulator.AbstractBank;
import com.moddynerd.transiq.payment.gateway.bank.simulator.AuthorizationSimulator;
import com.moddynerd.transiq.payment.gateway.model.BankCode;
import com.moddynerd.transiq.payment.gateway.model.BankType;
import com.moddynerd.transiq.payment.gateway.model.CardNetwork;

import java.util.Set;

public class SbiBank extends AbstractBank {

    public SbiBank(AuthorizationSimulator simulator) {
        super(
                new BankConfiguration(
                        BankCode.SBI,
                        0.92,
                        0.08,
                        7_50_000L,
                        Set.of(CardNetwork.VISA, CardNetwork.MASTERCARD, CardNetwork.RUPAY),
                        BankType.ISSUER
                ),
                simulator
        );
    }

}
