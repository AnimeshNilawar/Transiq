package com.moddynerd.transiq.webhook.service;

import com.moddynerd.transiq.auth.exception.ResourceNotFoundException;
import com.moddynerd.transiq.webhook.entity.WebhookDelivery;
import com.moddynerd.transiq.webhook.entity.WebhookDeliveryStatus;
import com.moddynerd.transiq.webhook.repository.WebhookDeliveryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WebhookDeliveryRetryServiceImpl
        implements WebhookDeliveryRetryService {

    private final WebhookDeliveryRepository repository;
    private final WebhookDeliveryExecutor executor;

    @Override
    public void retry(UUID deliveryId) {

        WebhookDelivery delivery = repository.findById(deliveryId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Webhook delivery not found: " + deliveryId
                        ));

        // reset status before replaying
        delivery.setStatus(WebhookDeliveryStatus.PENDING);
        delivery.setFailureReason(null);
        delivery.setHttpStatus(null);
        delivery.setResponseBody(null);
        delivery.setDeliveredAt(null);
        delivery.setNextRetryAt(null);
        executor.execute(delivery);
    }
}