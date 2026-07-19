package com.moddynerd.transiq.webhook.retry;

import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;

@Service
public class ExponentialWebhookRetryPolicy
        implements WebhookRetryPolicy {

    private static final int MAX_ATTEMPTS = 5;
    private static final long INITIAL_DELAY_MINUTES = 1;

    /**
     * Retry Schedule
     *
     * Attempt 1 -> 1 minute
     * Attempt 2 -> 2 minutes
     * Attempt 3 -> 4 minutes
     * Attempt 4 -> 8 minutes
     * Attempt 5 -> 16 minutes
     */
    @Override
    public boolean shouldRetry(int attemptCount) {
        return attemptCount < MAX_ATTEMPTS;
    }

    @Override
    public Instant nextRetryTime(int attemptCount) {

        long delayMinutes =
                INITIAL_DELAY_MINUTES * (1L << (attemptCount - 1));

        return Instant.now()
                .plus(Duration.ofMinutes(delayMinutes));
    }
}