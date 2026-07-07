package com.moddynerd.transiq.payment.processor;

import com.moddynerd.transiq.payment.entity.Payment;

public interface PaymentProcessor {

    void process(Payment payment);

}