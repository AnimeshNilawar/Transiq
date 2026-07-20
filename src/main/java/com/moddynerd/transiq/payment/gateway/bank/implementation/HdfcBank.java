package com.moddynerd.transiq.payment.gateway.bank.implementation;

import com.moddynerd.transiq.payment.gateway.bank.configuration.BankConfiguration;
import com.moddynerd.transiq.payment.gateway.bank.simulator.AbstractBank;
import com.moddynerd.transiq.payment.gateway.bank.simulator.AuthorizationSimulator;
import com.moddynerd.transiq.payment.gateway.model.BankCode;
import com.moddynerd.transiq.payment.gateway.model.BankType;
import com.moddynerd.transiq.payment.gateway.model.CardNetwork;

import java.util.Set;

public class HdfcBank extends AbstractBank {

    public HdfcBank(AuthorizationSimulator simulator) {
        super(
                new BankConfiguration(
                        BankCode.HDFC,
                        0.95,
                        0.05,
                        10_00_000L,
                        Set.of(CardNetwork.VISA, CardNetwork.MASTERCARD, CardNetwork.RUPAY),
                        BankType.ISSUER
                ),
                simulator
        );
    }

}
