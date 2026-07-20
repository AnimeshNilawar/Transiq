package com.moddynerd.transiq.payment.gateway.bank.simulator;

import com.moddynerd.transiq.payment.gateway.bank.Bank;
import com.moddynerd.transiq.payment.gateway.bank.configuration.BankConfiguration;
import com.moddynerd.transiq.payment.gateway.model.AuthorizationRequest;
import com.moddynerd.transiq.payment.gateway.model.AuthorizationResponse;
import com.moddynerd.transiq.payment.gateway.model.BankCode;
import lombok.Getter;

@Getter
public abstract class AbstractBank implements Bank {

    private final BankConfiguration configuration;
    private final AuthorizationSimulator simulator;

    protected AbstractBank(
            BankConfiguration configuration,
            AuthorizationSimulator simulator
    ) {
        this.configuration = configuration;
        this.simulator = simulator;
    }

    @Override
    public BankCode code() {
        return configuration.bankCode();
    }

    @Override
    public AuthorizationResponse authorize(AuthorizationRequest request) {
        return simulator.authorize(request, configuration);
    }

}
