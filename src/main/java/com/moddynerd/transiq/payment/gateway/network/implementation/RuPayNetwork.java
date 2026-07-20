package com.moddynerd.transiq.payment.gateway.network.implementation;

import com.moddynerd.transiq.payment.gateway.bank.Bank;
import com.moddynerd.transiq.payment.gateway.model.AuthorizationRequest;
import com.moddynerd.transiq.payment.gateway.model.AuthorizationResponse;
import com.moddynerd.transiq.payment.gateway.model.CardNetwork;
import com.moddynerd.transiq.payment.gateway.network.PaymentNetwork;
import com.moddynerd.transiq.payment.gateway.network.resolver.IssuerResolver;
import com.moddynerd.transiq.payment.gateway.registry.BankRegistry;
import org.springframework.stereotype.Component;

@Component
public class RuPayNetwork implements PaymentNetwork {

    private final IssuerResolver issuerResolver;
    private final BankRegistry bankRegistry;

    public RuPayNetwork(
            IssuerResolver issuerResolver,
            BankRegistry bankRegistry
    ) {
        this.issuerResolver = issuerResolver;
        this.bankRegistry = bankRegistry;
    }

    @Override
    public CardNetwork network() {
        return CardNetwork.RUPAY;
    }

    @Override
    public AuthorizationResponse authorize(AuthorizationRequest request) {
        var bankCode = issuerResolver.resolve(request);
        Bank bank = bankRegistry.get(bankCode);
        return bank.authorize(request);
    }

}
