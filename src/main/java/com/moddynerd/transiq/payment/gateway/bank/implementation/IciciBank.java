package com.moddynerd.transiq.payment.gateway.bank.implementation;

import com.moddynerd.transiq.payment.gateway.bank.configuration.BankConfiguration;
import com.moddynerd.transiq.payment.gateway.bank.simulator.AbstractBank;
import com.moddynerd.transiq.payment.gateway.bank.simulator.AuthorizationSimulator;
import com.moddynerd.transiq.payment.gateway.model.BankCode;
import com.moddynerd.transiq.payment.gateway.model.BankType;
import com.moddynerd.transiq.payment.gateway.model.CardNetwork;

import java.util.Set;

public class IciciBank extends AbstractBank {

    public IciciBank(AuthorizationSimulator simulator) {
        super(
                new BankConfiguration(
                        BankCode.ICICI,
                        0.90,
                        0.10,
                        5_00_000L,
                        Set.of(CardNetwork.VISA, CardNetwork.MASTERCARD),
                        BankType.ISSUER
                ),
                simulator
        );
    }

}
