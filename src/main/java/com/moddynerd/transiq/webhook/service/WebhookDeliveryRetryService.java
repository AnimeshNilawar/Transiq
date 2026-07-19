package com.moddynerd.transiq.webhook.service;

import java.util.UUID;

public interface WebhookDeliveryRetryService {

    void retry(UUID deliveryId);

}