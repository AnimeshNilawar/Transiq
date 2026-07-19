package com.moddynerd.transiq.webhook.signer;

import com.moddynerd.transiq.webhook.exception.WebhookSignatureException;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

import org.springframework.stereotype.Service;

@Service
public class HmacWebhookSigner
        implements WebhookSigner {

    private static final String HMAC_ALGORITHM = "HmacSHA256";

    @Override
    public String sign(
            String payload,
            String secret
    ) {

        try {

            Mac mac =
                    Mac.getInstance(HMAC_ALGORITHM);

            SecretKeySpec key =
                    new SecretKeySpec(
                            secret.getBytes(StandardCharsets.UTF_8),
                            HMAC_ALGORITHM
                    );

            mac.init(key);

            byte[] signature =
                    mac.doFinal(
                            payload.getBytes(StandardCharsets.UTF_8)
                    );

            return toHex(signature);

        } catch (Exception ex) {

            throw new WebhookSignatureException(
                    "Unable to generate webhook signature",
                    ex
            );

        }

    }

    private String toHex(byte[] bytes) {

        StringBuilder builder = new StringBuilder();

        for (byte b : bytes) {
            builder.append(
                    String.format("%02x", b)
            );
        }

        return builder.toString();

    }

}