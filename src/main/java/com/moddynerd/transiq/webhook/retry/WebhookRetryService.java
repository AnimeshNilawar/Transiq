package com.moddynerd.transiq.webhook.retry;

public interface WebhookRetryService {

    void retryPendingDeliveries();

}