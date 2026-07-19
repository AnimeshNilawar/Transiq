package com.moddynerd.transiq.webhook.service;

import com.moddynerd.transiq.webhook.entity.WebhookDelivery;
import com.moddynerd.transiq.webhook.entity.WebhookEndpoint;
import com.moddynerd.transiq.webhook.entity.WebhookEvent;
import org.springframework.http.ResponseEntity;

public interface WebhookDeliveryService {

    WebhookDelivery createDelivery(
            WebhookEndpoint endpoint,
            WebhookEvent event
    );

    void markDelivered(
            WebhookDelivery delivery,
            ResponseEntity<?> response,
            long duration
    );

    void markFailed(
            WebhookDelivery delivery,
            Exception exception,
            long duration
    );

    void handleDeliveryFailure(
            WebhookDelivery delivery,
            Exception exception,
            long duration
    );

}
