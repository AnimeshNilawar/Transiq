package com.moddynerd.transiq.webhook.sender;

import com.moddynerd.transiq.shared.security.SecretEncryptionService;
import com.moddynerd.transiq.webhook.entity.WebhookDelivery;
import com.moddynerd.transiq.webhook.entity.WebhookEndpoint;
import com.moddynerd.transiq.webhook.signer.WebhookSigner;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class HttpWebhookSender implements WebhookSender {

    private final RestClient restClient;
    private final SecretEncryptionService encryptionService;
    private final WebhookSigner webhookSigner;

    @Override
    public ResponseEntity<?> send(WebhookDelivery delivery) {

        WebhookEndpoint endpoint = delivery.getEndpoint();
        String payloadJson = delivery.getEvent().getPayload();

        long timestamp = Instant.now().getEpochSecond();

        String secret = encryptionService.decrypt(
                endpoint.getEncryptedSecret()
        );

        String signedPayload = timestamp + "." + payloadJson;

        String signature = webhookSigner.sign(
                signedPayload,
                secret
        );

        return restClient.post()
                .uri(endpoint.getUrl())
                .header(
                        "X-Transiq-Signature",
                        "sha256=" + signature
                )
                .header(
                        "X-Transiq-Timestamp",
                        String.valueOf(timestamp)
                )
                .contentType(MediaType.APPLICATION_JSON)
                .body(payloadJson)
                .retrieve()
                .toBodilessEntity();
    }

}
