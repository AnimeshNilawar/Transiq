package com.moddynerd.transiq.shared.util;

import com.moddynerd.transiq.apikey.entity.ApiKeyEnvironment;
import com.moddynerd.transiq.apikey.entity.ApiKeyType;

import java.security.SecureRandom;

public final class ApiKeyGenerator {
    private static final SecureRandom RANDOM = new SecureRandom();

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    private static final int SECRET_LENGTH = 32;

    private ApiKeyGenerator(){}

    public static String generate(ApiKeyType type, ApiKeyEnvironment environment){
        String prefix = switch (type){
            case SECRET -> "sk";
            case RESTRICTED -> "rk";
            case PUBLISHABLE -> "pk";
        };

        String env = environment == ApiKeyEnvironment.LIVE ? "live" : "test";

        StringBuilder builder = new StringBuilder();

        builder.append(prefix)
                .append("_")
                .append(env)
                .append("_");

        for(int i=0; i < SECRET_LENGTH; i++){
            builder.append(CHARACTERS.charAt(
                    RANDOM.nextInt(CHARACTERS.length())
            )
            );
        }
        return builder.toString();
    }

    public static String getPrefix(String apiKey){
        return apiKey.substring(0, 16);
    }
}
