package com.moddynerd.transiq.webhook.service;

import com.moddynerd.transiq.webhook.entity.WebhookDelivery;
import com.moddynerd.transiq.webhook.entity.WebhookDeliveryStatus;
import com.moddynerd.transiq.webhook.entity.WebhookEndpoint;
import com.moddynerd.transiq.webhook.entity.WebhookEvent;
import com.moddynerd.transiq.webhook.repository.WebhookDeliveryRepository;
import com.moddynerd.transiq.webhook.retry.WebhookRetryPolicy;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class WebhookDeliveryServiceImpl implements WebhookDeliveryService {

    private final WebhookDeliveryRepository deliveryRepository;
    private final WebhookRetryPolicy retryPolicy;

    @Override
    public WebhookDelivery createDelivery(
            WebhookEndpoint endpoint,
            WebhookEvent event
    ) {

        WebhookDelivery delivery = WebhookDelivery.builder()
                .endpoint(endpoint)
                .event(event)
                .eventReference(event.getReference())
                .eventType(event.getEventType().name().toLowerCase().replace('_', '.'))
                .status(WebhookDeliveryStatus.PENDING)
                .attemptCount(0)
                .build();

        return deliveryRepository.save(delivery);
    }

    @Override
    public void markDelivered(
            WebhookDelivery delivery,
            ResponseEntity<?> response,
            long duration
    ) {
        delivery.setStatus(WebhookDeliveryStatus.DELIVERED);
        delivery.setHttpStatus(response.getStatusCode().value());
        delivery.setDurationMs(duration);
        delivery.setDeliveredAt(Instant.now());

        deliveryRepository.save(delivery);
    }

    @Override
    public void markFailed(
            WebhookDelivery delivery,
            Exception exception,
            long duration
    ) {
        delivery.setStatus(WebhookDeliveryStatus.FAILED);
        delivery.setDurationMs(duration);
        delivery.setDeliveredAt(Instant.now());
        delivery.setResponseBody(exception.getMessage());

        deliveryRepository.save(delivery);
    }

    @Override
    public void handleDeliveryFailure(
            WebhookDelivery delivery,
            Exception exception,
            long duration
    ) {

        delivery.setAttemptCount(
                delivery.getAttemptCount() + 1
        );

        delivery.setDurationMs(duration);
        delivery.setLastAttemptAt(Instant.now());
        delivery.setFailureReason(exception.getMessage());

        if (retryPolicy.shouldRetry(delivery.getAttemptCount())) {

            delivery.setNextRetryAt(
                    retryPolicy.nextRetryTime(
                            delivery.getAttemptCount()
                    )
            );

            delivery.setStatus(
                    WebhookDeliveryStatus.PENDING
            );

        } else {

            delivery.setStatus(
                    WebhookDeliveryStatus.FAILED
            );

        }

        deliveryRepository.save(delivery);

    }

}
