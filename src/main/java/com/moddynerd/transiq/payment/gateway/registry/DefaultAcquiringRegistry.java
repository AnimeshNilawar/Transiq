package com.moddynerd.transiq.payment.gateway.registry;

import com.moddynerd.transiq.payment.gateway.acquirer.AcquiringBank;
import com.moddynerd.transiq.payment.gateway.acquirer.AcquiringBankCode;
import com.moddynerd.transiq.payment.gateway.exception.AcquiringBankNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class DefaultAcquiringRegistry implements AcquiringRegistry {

    private final Map<AcquiringBankCode, AcquiringBank> banks;

    public DefaultAcquiringRegistry(Collection<AcquiringBank> bankList) {
        this.banks = bankList.stream()
                .collect(Collectors.toMap(
                        AcquiringBank::getCode,
                        Function.identity()
                ));
    }

    @Override
    public AcquiringBank get(AcquiringBankCode code) {
        AcquiringBank bank = banks.get(code);
        if (bank == null) {
            throw new AcquiringBankNotFoundException(code);
        }
        return bank;
    }

    @Override
    public boolean exists(AcquiringBankCode code) {
        return banks.containsKey(code);
    }

    @Override
    public Collection<AcquiringBank> getAll() {
        return banks.values();
    }

}
