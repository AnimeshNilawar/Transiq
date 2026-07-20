package com.moddynerd.transiq.payment.expiration;

import com.moddynerd.transiq.payment.entity.Payment;

public interface PaymentExpirationService {
    void validate(Payment payment);
}
