package com.moddynerd.transiq.webhook.service;

import com.moddynerd.transiq.webhook.entity.WebhookDelivery;

public interface WebhookDeliveryExecutor {

    void execute(WebhookDelivery delivery);

}