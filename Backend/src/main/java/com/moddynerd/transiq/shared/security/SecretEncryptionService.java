package com.moddynerd.transiq.shared.security;


public interface SecretEncryptionService {

    String encrypt(String plainText);

    String decrypt(String cipherText);

}