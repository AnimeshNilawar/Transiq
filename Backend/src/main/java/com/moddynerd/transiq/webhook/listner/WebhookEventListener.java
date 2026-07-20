package com.moddynerd.transiq.webhook.listner;

import com.moddynerd.transiq.event.payment.PaymentSucceededEvent;
import com.moddynerd.transiq.webhook.dispatcher.WebhookDispatcher;
import com.moddynerd.transiq.webhook.dto.WebhookPayload;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class WebhookEventListener {
    private final WebhookDispatcher webhookDispatcher;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handlePaymentSucceeded(PaymentSucceededEvent event){
        WebhookPayload payload = new WebhookPayload(
                UUID.randomUUID(),
                "payment.succeeded",
                event.paymentReference(),
                event.occurredAt(),
                event
        );

        webhookDispatcher.dispatch(payload, event.merchantId());
    }
}
