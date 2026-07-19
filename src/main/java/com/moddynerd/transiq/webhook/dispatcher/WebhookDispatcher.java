package com.moddynerd.transiq.webhook.dispatcher;

import com.moddynerd.transiq.webhook.dto.WebhookPayload;

import java.util.UUID;

public interface WebhookDispatcher {

    void dispatch(WebhookPayload payload, UUID merchantId);

}