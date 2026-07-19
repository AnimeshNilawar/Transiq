package com.moddynerd.transiq.webhook.service;

import com.moddynerd.transiq.webhook.dto.WebhookPayload;
import com.moddynerd.transiq.webhook.entity.WebhookEvent;

import java.util.UUID;

public interface WebhookEventService {

    WebhookEvent createEvent(
            WebhookPayload payload,
            UUID merchantId
    );

    WebhookEvent findByEventId(UUID eventId);

    WebhookEvent findById(UUID id);

}
