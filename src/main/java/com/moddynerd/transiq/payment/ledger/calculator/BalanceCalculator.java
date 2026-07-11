package com.moddynerd.transiq.payment.ledger.calculator;

import com.moddynerd.transiq.payment.ledger.entity.EntrySide;
import com.moddynerd.transiq.payment.ledger.entity.LedgerAccount;
import com.moddynerd.transiq.payment.ledger.entity.LedgerEntry;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class BalanceCalculator {
    public BigDecimal calculateMerchantBalance(List<LedgerEntry> entries){
        BigDecimal balance = BigDecimal.ZERO;

        for(LedgerEntry entry : entries){
            if(entry.getAccount() != LedgerAccount.MERCHANT_PAYABLE){continue;}

            if(entry.getSide() == EntrySide.CREDIT){
                balance = balance.add(entry.getAmount());
            } else {
                balance = balance.subtract(entry.getAmount());
            }
        }

        return balance;
    }
}
