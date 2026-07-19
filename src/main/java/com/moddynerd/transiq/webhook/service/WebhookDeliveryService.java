package com.moddynerd.transiq.webhook.service;

import com.moddynerd.transiq.webhook.dto.WebhookPayload;
import com.moddynerd.transiq.webhook.entity.WebhookDelivery;
import com.moddynerd.transiq.webhook.entity.WebhookEndpoint;
import org.springframework.http.ResponseEntity;

public interface WebhookDeliveryService {

    WebhookDelivery createDelivery(
            WebhookEndpoint endpoint,
            WebhookPayload payload
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

}