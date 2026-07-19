package com.moddynerd.transiq.webhook.scheduler;

import com.moddynerd.transiq.webhook.retry.WebhookRetryService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WebhookRetryScheduler {

    private final WebhookRetryService retryService;

    @Scheduled(fixedDelay = 30000)
    public void processRetries() {

        retryService.retryPendingDeliveries();

    }

}