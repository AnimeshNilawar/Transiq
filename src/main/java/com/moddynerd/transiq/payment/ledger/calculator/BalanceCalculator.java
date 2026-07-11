package com.moddynerd.transiq.payment.ledger.calculator;

import com.moddynerd.transiq.payment.ledger.entity.EntrySide;
import com.moddynerd.transiq.payment.ledger.entity.LedgerAccount;
import com.moddynerd.transiq.payment.ledger.entity.LedgerEntry;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BalanceCalculator {
    public Long calculateMerchantBalance(List<LedgerEntry> entries){
        long balance = 0L;

        for(LedgerEntry entry : entries){
            if(entry.getAccount() != LedgerAccount.MERCHANT_PAYABLE){continue;}

            if(entry.getSide() == EntrySide.CREDIT){
                balance += entry.getAmount();
            } else {
                balance -= entry.getAmount();
            }
        }

        return balance;
    }
}
