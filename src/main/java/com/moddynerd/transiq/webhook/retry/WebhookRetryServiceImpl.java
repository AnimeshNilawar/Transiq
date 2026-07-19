package com.moddynerd.transiq.webhook.retry;

import com.moddynerd.transiq.webhook.entity.WebhookDelivery;
import com.moddynerd.transiq.webhook.entity.WebhookDeliveryStatus;
import com.moddynerd.transiq.webhook.repository.WebhookDeliveryRepository;
import com.moddynerd.transiq.webhook.sender.WebhookSender;
import com.moddynerd.transiq.webhook.service.WebhookDeliveryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebhookRetryServiceImpl implements WebhookRetryService {
    private final WebhookDeliveryRepository repository;
    private final WebhookSender webhookSender;
    private final WebhookDeliveryService deliveryService;

    @Override
    public void retryPendingDeliveries() {
        List<WebhookDelivery> deliveries =
                repository.findByStatusAndNextRetryAtLessThanEqual(
                        WebhookDeliveryStatus.PENDING,
                        Instant.now()
                );

        for (WebhookDelivery delivery : deliveries){
            long start = System.currentTimeMillis();

            try {

                ResponseEntity<?> response =
                        webhookSender.send(delivery);

                deliveryService.markDelivered(
                        delivery,
                        response,
                        System.currentTimeMillis() - start
                );

            } catch (Exception ex) {

                deliveryService.handleDeliveryFailure(
                        delivery,
                        ex,
                        System.currentTimeMillis() - start
                );

            }
        }
    }
}