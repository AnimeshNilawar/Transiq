package com.moddynerd.transiq.webhook.sender;

import com.moddynerd.transiq.webhook.entity.WebhookDelivery;
import org.springframework.http.ResponseEntity;

public interface WebhookSender {

    ResponseEntity<?> send(WebhookDelivery delivery);

}