package com.moddynerd.transiq.payment.ledger.service;

import com.moddynerd.transiq.merchant.entity.Merchant;
import com.moddynerd.transiq.payment.ledger.calculator.BalanceCalculator;
import com.moddynerd.transiq.payment.ledger.dto.MerchantBalanceResponse;
import com.moddynerd.transiq.payment.ledger.entity.LedgerEntry;
import com.moddynerd.transiq.payment.ledger.repository.LedgerEntryRepository;
import com.moddynerd.transiq.shared.security.CurrentApiKeyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MerchantBalanceServiceImpl implements MerchantBalanceService {

    private final LedgerEntryRepository ledgerEntryRepository;
    private final BalanceCalculator balanceCalculator;
    private final CurrentApiKeyService currentApiKeyService;

    @Override
    public MerchantBalanceResponse getBalance() {
        Merchant merchant = currentApiKeyService.getCurrentPrincipal().merchant();

        List<LedgerEntry> entries = ledgerEntryRepository.findAllByMerchantOrderByCreatedAtAsc(merchant);

        BigDecimal balance = balanceCalculator.calculateMerchantBalance(entries);

        return new MerchantBalanceResponse(balance, "INR");
    }
}
