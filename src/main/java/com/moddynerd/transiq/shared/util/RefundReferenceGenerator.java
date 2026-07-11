package com.moddynerd.transiq.shared.util;

import java.security.SecureRandom;

public final class RefundReferenceGenerator {

    private static final SecureRandom RANDOM = new SecureRandom();

    private RefundReferenceGenerator() {}

    public static String generate() {

        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

        StringBuilder builder = new StringBuilder("rfnd_");

        for (int i = 0; i < 16; i++) {
            builder.append(
                    chars.charAt(
                            RANDOM.nextInt(chars.length())
                    )
            );
        }

        return builder.toString();
    }
}