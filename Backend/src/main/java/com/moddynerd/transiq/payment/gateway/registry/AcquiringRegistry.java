package com.moddynerd.transiq.payment.gateway.registry;

import com.moddynerd.transiq.payment.gateway.acquirer.AcquiringBank;
import com.moddynerd.transiq.payment.gateway.acquirer.AcquiringBankCode;

import java.util.Collection;

public interface AcquiringRegistry {

    AcquiringBank get(AcquiringBankCode code);

    boolean exists(AcquiringBankCode code);

    Collection<AcquiringBank> getAll();

}
