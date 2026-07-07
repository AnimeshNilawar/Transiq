package com.moddynerd.transiq.shared.util;

import java.security.SecureRandom;

public final class ClientSecretGenerator {

    private static final SecureRandom RANDOM = new SecureRandom();

    private static final String CHARACTERS =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    private ClientSecretGenerator() {
    }

    public static String generate(String paymentReference) {

        StringBuilder builder = new StringBuilder();

        builder.append(paymentReference)
                .append("_secret_");

        for (int i = 0; i < 32; i++) {

            builder.append(
                    CHARACTERS.charAt(
                            RANDOM.nextInt(CHARACTERS.length())
                    )
            );
        }

        return builder.toString();
    }

}