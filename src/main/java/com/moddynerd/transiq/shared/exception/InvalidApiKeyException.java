package com.moddynerd.transiq.shared.exception;

import com.moddynerd.transiq.auth.exception.UnauthorizedException;

public class InvalidApiKeyException extends UnauthorizedException {

    public InvalidApiKeyException(String message) {
        super(message);
    }
}