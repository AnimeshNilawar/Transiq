package com.moddynerd.transiq.shared.util;

import java.security.SecureRandom;

public class TempPasswordGenerator {

    private static final String LOWERCASE = "abcdefghijkmnpqrstuvwxyz";
    private static final String UPPERCASE = "ABCDEFGHJKLMNPQRSTUVWXYZ";
    private static final String DIGITS = "23456789";
    private static final String SPECIAL = "!@#$%^&*";
    private static final String ALL = LOWERCASE + UPPERCASE + DIGITS + SPECIAL;
    private static final int LENGTH = 12;
    private static final SecureRandom RANDOM = new SecureRandom();

    private TempPasswordGenerator() {
    }

    public static String generate() {
        StringBuilder password = new StringBuilder(LENGTH);

        password.append(LOWERCASE.charAt(RANDOM.nextInt(LOWERCASE.length())));
        password.append(UPPERCASE.charAt(RANDOM.nextInt(UPPERCASE.length())));
        password.append(DIGITS.charAt(RANDOM.nextInt(DIGITS.length())));
        password.append(SPECIAL.charAt(RANDOM.nextInt(SPECIAL.length())));

        for (int i = 4; i < LENGTH; i++) {
            password.append(ALL.charAt(RANDOM.nextInt(ALL.length())));
        }

        char[] chars = password.toString().toCharArray();
        for (int i = chars.length - 1; i > 0; i--) {
            int j = RANDOM.nextInt(i + 1);
            char tmp = chars[i];
            chars[i] = chars[j];
            chars[j] = tmp;
        }

        return new String(chars);
    }
}
