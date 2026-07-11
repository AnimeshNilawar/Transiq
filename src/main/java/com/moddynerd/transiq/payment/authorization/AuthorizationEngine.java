package com.moddynerd.transiq.payment.authorization;

import com.moddynerd.transiq.payment.entity.Payment;

public interface AuthorizationEngine {
    AuthorizationResult authorize(Payment payment);
}
