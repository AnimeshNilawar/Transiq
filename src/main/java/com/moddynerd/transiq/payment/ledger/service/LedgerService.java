package com.moddynerd.transiq.payment.ledger.service;

import com.moddynerd.transiq.payment.entity.Payment;

public interface LedgerService {

    void recordSuccessfulPayment(Payment payment);

}