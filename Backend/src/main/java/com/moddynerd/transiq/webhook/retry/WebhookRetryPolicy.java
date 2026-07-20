package com.moddynerd.transiq.webhook.retry;

import java.time.Instant;

public interface WebhookRetryPolicy {

    boolean shouldRetry(int attemptCount);

    Instant nextRetryTime(int attemptCount);

}