package com.moddynerd.transiq.shared.util;

import java.util.UUID;

public class WebhookSecretGenerator {

    private WebhookSecretGenerator() {
    }

    public static String generate() {

        return "whsec_" +
                UUID.randomUUID()
                        .toString()
                        .replace("-", "");

    }

}