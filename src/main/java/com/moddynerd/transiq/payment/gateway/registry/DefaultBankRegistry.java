package com.moddynerd.transiq.payment.gateway.registry;

import com.moddynerd.transiq.payment.gateway.bank.Bank;
import com.moddynerd.transiq.payment.gateway.exception.BankNotFoundException;
import com.moddynerd.transiq.payment.gateway.model.BankCode;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class DefaultBankRegistry implements BankRegistry {

    private final Map<BankCode, Bank> banks;

    public DefaultBankRegistry(Collection<Bank> bankList) {
        this.banks = bankList.stream()
                .collect(Collectors.toMap(
                        Bank::code,
                        Function.identity()
                ));
    }

    @Override
    public Bank get(BankCode bankCode) {
        Bank bank = banks.get(bankCode);
        if (bank == null) {
            throw new BankNotFoundException(bankCode);
        }
        return bank;
    }

    @Override
    public boolean contains(BankCode bankCode) {
        return banks.containsKey(bankCode);
    }

    @Override
    public Collection<Bank> getAll() {
        return banks.values();
    }

}
