package com.moddynerd.transiq.payment.ledger.service;

import com.moddynerd.transiq.payment.ledger.dto.MerchantBalanceResponse;

public interface MerchantBalanceService {

    MerchantBalanceResponse getBalance();

}