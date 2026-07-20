package com.moddynerd.transiq.webhook.retry;

import com.moddynerd.transiq.webhook.entity.WebhookDelivery;
import com.moddynerd.transiq.webhook.entity.WebhookDeliveryStatus;
import com.moddynerd.transiq.webhook.repository.WebhookDeliveryRepository;
import com.moddynerd.transiq.webhook.service.WebhookDeliveryExecutor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebhookRetryServiceImpl implements WebhookRetryService {

    private final WebhookDeliveryRepository repository;
    private final WebhookDeliveryExecutor executor;

    @Override
    public void retryPendingDeliveries() {
        List<WebhookDelivery> deliveries =
                repository.findByStatusAndNextRetryAtLessThanEqual(
                        WebhookDeliveryStatus.PENDING,
                        Instant.now()
                );

        deliveries.forEach(executor::execute);
    }

}
