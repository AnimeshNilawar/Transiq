package com.moddynerd.transiq.payment.ledger.controller;

import com.moddynerd.transiq.payment.ledger.dto.MerchantBalanceResponse;
import com.moddynerd.transiq.payment.ledger.service.MerchantBalanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/ledger")
@RequiredArgsConstructor
public class LedgerController {

    private final MerchantBalanceService merchantBalanceService;

    @GetMapping("/balance")
    public MerchantBalanceResponse balance() {

        return merchantBalanceService.getBalance();
    }
}