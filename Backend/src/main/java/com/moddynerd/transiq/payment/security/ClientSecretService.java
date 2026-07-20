package com.moddynerd.transiq.payment.security;

import com.moddynerd.transiq.payment.entity.Payment;

public interface ClientSecretService {

    void verify(
            Payment payment,
            String clientSecret
    );

}