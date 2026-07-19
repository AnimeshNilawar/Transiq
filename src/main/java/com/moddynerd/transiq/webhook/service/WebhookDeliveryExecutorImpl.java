package com.moddynerd.transiq.webhook.service;

import com.moddynerd.transiq.webhook.entity.WebhookDelivery;
import com.moddynerd.transiq.webhook.sender.WebhookSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebhookDeliveryExecutorImpl
        implements WebhookDeliveryExecutor {

    private final WebhookSender webhookSender;
    private final WebhookDeliveryService deliveryService;

    @Override
    public void execute(WebhookDelivery delivery) {

        delivery.setAttemptCount(delivery.getAttemptCount() + 1);

        long start = System.currentTimeMillis();

        try {

            ResponseEntity<?> response =
                    webhookSender.send(delivery);

            deliveryService.markDelivered(
                    delivery,
                    response,
                    System.currentTimeMillis() - start
            );

            log.info(
                    "Webhook {} delivered successfully.",
                    delivery.getId()
            );

        } catch (Exception ex) {

            deliveryService.handleDeliveryFailure(
                    delivery,
                    ex,
                    System.currentTimeMillis() - start
            );

            log.warn(
                    "Webhook {} delivery failed.",
                    delivery.getId(),
                    ex
            );

        }

    }

}