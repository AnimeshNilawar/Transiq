package com.moddynerd.transiq.payment.gateway.bin;

public class BinNotFoundException extends RuntimeException {

    public BinNotFoundException(String bin) {
        super("BIN not found: " + bin);
    }

}
