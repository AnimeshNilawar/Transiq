package com.moddynerd.transiq.shared.security;

import com.moddynerd.transiq.shared.exception.EncryptionException;
import com.moddynerd.transiq.shared.properties.SecurityProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

@Service
@RequiredArgsConstructor
@Slf4j
public class AesSecretEncryptionService implements SecretEncryptionService {

    private final SecurityProperties securityProperties;

    private static final String ALGORITHM = "AES/GCM/NoPadding";

    private static final int GCM_TAG_LENGTH = 128;

    private static final int IV_LENGTH = 12;

    private SecretKey getSecretKey() {

        return new SecretKeySpec(
                securityProperties.getEncryptionKey()
                        .getBytes(StandardCharsets.UTF_8),
                "AES"
        );

    }

    @Override
    public String encrypt(String plainText) {

        try {

            Cipher cipher = Cipher.getInstance(ALGORITHM);

            byte[] iv = new byte[IV_LENGTH];

            new SecureRandom().nextBytes(iv);

            cipher.init(
                    Cipher.ENCRYPT_MODE,
                    getSecretKey(),
                    new GCMParameterSpec(
                            GCM_TAG_LENGTH,
                            iv
                    )
            );

            byte[] encrypted =
                    cipher.doFinal(
                            plainText.getBytes(StandardCharsets.UTF_8)
                    );

            ByteBuffer buffer =
                    ByteBuffer.allocate(
                            iv.length + encrypted.length
                    );

            buffer.put(iv);
            buffer.put(encrypted);

            return Base64.getEncoder()
                    .encodeToString(buffer.array());

        } catch (Exception ex) {

            log.error("Failed to encrypt webhook secret", ex);

            throw new EncryptionException(
                    "Unable to encrypt webhook secret",
                    ex
            );

        }

    }

    @Override
    public String decrypt(String cipherText) {

        try {

            byte[] decoded =
                    Base64.getDecoder().decode(cipherText);

            ByteBuffer buffer =
                    ByteBuffer.wrap(decoded);

            byte[] iv = new byte[IV_LENGTH];
            buffer.get(iv);

            byte[] encrypted =
                    new byte[buffer.remaining()];
            buffer.get(encrypted);

            Cipher cipher =
                    Cipher.getInstance(ALGORITHM);

            cipher.init(
                    Cipher.DECRYPT_MODE,
                    getSecretKey(),
                    new GCMParameterSpec(
                            GCM_TAG_LENGTH,
                            iv
                    )
            );

            byte[] decrypted =
                    cipher.doFinal(encrypted);

            return new String(
                    decrypted,
                    StandardCharsets.UTF_8
            );

        } catch (Exception ex) {

            throw new EncryptionException(
                    "Unable to decrypt webhook secret",
                    ex
            );

        }

    }

}