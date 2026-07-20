package com.moddynerd.transiq.payment.gateway.routing;

import com.moddynerd.transiq.payment.gateway.acquirer.AcquiringBankCode;
import com.moddynerd.transiq.payment.gateway.model.BankCode;
import com.moddynerd.transiq.payment.gateway.model.CardNetwork;

public record RoutingDecision(
        AcquiringBankCode acquirer,
        CardNetwork network,
        BankCode issuerBank,
        RoutingReason reason
) {}
