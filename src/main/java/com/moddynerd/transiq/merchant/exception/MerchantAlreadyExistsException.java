package com.moddynerd.transiq.merchant.exception;

public class MerchantAlreadyExistsException extends RuntimeException {

    public MerchantAlreadyExistsException(String email) {
        super("Merchant already exists with email: " + email);
    }
}