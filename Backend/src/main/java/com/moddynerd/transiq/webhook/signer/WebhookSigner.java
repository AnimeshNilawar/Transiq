package com.moddynerd.transiq.webhook.signer;


public interface WebhookSigner {

    String sign(
            String payload,
            String secret
    );

}