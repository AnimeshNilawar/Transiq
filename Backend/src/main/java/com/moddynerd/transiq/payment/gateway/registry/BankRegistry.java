package com.moddynerd.transiq.payment.gateway.registry;

import com.moddynerd.transiq.payment.gateway.bank.Bank;
import com.moddynerd.transiq.payment.gateway.model.BankCode;

import java.util.Collection;

public interface BankRegistry {

    Bank get(BankCode bankCode);

    boolean contains(BankCode bankCode);

    Collection<Bank> getAll();

}
