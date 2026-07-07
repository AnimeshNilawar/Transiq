package com.moddynerd.transiq.shared.util;

import java.security.SecureRandom;

public final class PaymentReferenceGenerator {

    private static final SecureRandom RANDOM = new SecureRandom();

    private static final String CHARACTERS =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    private PaymentReferenceGenerator() {
    }

    public static String generate() {

        StringBuilder builder = new StringBuilder("pay_");

        for (int i = 0; i < 26; i++) {
            builder.append(
                    CHARACTERS.charAt(
                            RANDOM.nextInt(CHARACTERS.length())
                    )
            );
        }

        return builder.toString();
    }

}