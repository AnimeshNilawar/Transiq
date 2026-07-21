package com.moddynerd.transiq.webhook.mapper;

import com.moddynerd.transiq.webhook.entity.WebhookEventType;
import org.springframework.stereotype.Component;

@Component
public class WebhookEventTypeMapper {

    public WebhookEventType toEventType(
            String type
    ) {

        return switch (type) {

            case "payment.succeeded" ->
                    WebhookEventType.PAYMENT_SUCCEEDED;

            case "payment.failed" ->
                    WebhookEventType.PAYMENT_FAILED;

            case "refund.succeeded" ->
                    WebhookEventType.REFUND_SUCCEEDED;

            case "settlement.completed" ->
                    WebhookEventType.SETTLEMENT_COMPLETED;

            case "chargeback.created" ->
                    WebhookEventType.CHARGEBACK_CREATED;

            default ->
                    throw new IllegalArgumentException(
                            "Unknown webhook type: " + type
                    );
        };

    }
}
