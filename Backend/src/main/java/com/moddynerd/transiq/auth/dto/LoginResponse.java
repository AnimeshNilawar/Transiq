package com.moddynerd.transiq.auth.dto;

public record LoginResponse(
        String accessToken,
        String tokenType
) {
}