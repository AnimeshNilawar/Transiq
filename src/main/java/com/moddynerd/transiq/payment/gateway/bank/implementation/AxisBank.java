package com.moddynerd.transiq.payment.gateway.bank.implementation;

import com.moddynerd.transiq.payment.gateway.bank.configuration.BankConfiguration;
import com.moddynerd.transiq.payment.gateway.bank.simulator.AbstractBank;
import com.moddynerd.transiq.payment.gateway.bank.simulator.AuthorizationSimulator;
import com.moddynerd.transiq.payment.gateway.model.BankCode;
import com.moddynerd.transiq.payment.gateway.model.BankType;
import com.moddynerd.transiq.payment.gateway.model.CardNetwork;

import java.util.Set;

public class AxisBank extends AbstractBank {

    public AxisBank(AuthorizationSimulator simulator) {
        super(
                new BankConfiguration(
                        BankCode.AXIS,
                        0.85,
                        0.15,
                        2_00_000L,
                        Set.of(CardNetwork.VISA, CardNetwork.RUPAY),
                        BankType.ISSUER
                ),
                simulator
        );
    }

}
