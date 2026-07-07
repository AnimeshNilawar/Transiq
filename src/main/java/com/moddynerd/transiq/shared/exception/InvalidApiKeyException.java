package com.moddynerd.transiq.shared.exception;

public class InvalidApiKeyException extends UnauthorizedException {

    public InvalidApiKeyException(String message) {
        super(message);
    }
}