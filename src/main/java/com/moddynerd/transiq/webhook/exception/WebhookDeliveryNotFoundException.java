package com.moddynerd.transiq.webhook.exception;

import java.util.UUID;

public class WebhookDeliveryNotFoundException extends RuntimeException {

    public WebhookDeliveryNotFoundException(UUID id) {
        super("Webhook delivery not found: " + id);
    }

}