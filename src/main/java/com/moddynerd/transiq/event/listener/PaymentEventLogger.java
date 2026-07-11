package com.moddynerd.transiq.event.listener;

import com.moddynerd.transiq.event.payment.PaymentSucceededEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PaymentEventLogger {

    @EventListener
    public void onPaymentSucceeded(
            PaymentSucceededEvent event
    ) {

        log.info(
                "Payment Event Received: {}",
                event.paymentReference()
        );

    }

}