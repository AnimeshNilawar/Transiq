package com.moddynerd.transiq.payment.security;

import com.moddynerd.transiq.shared.exception.UnauthorizedException;
import com.moddynerd.transiq.payment.entity.Payment;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClientSecretServiceImpl
        implements ClientSecretService {

    private final PasswordEncoder passwordEncoder;

    @Override
    public void verify(
            Payment payment,
            String clientSecret
    ) {

        if (clientSecret == null || clientSecret.isBlank()) {
            throw new UnauthorizedException(
                    "Client Secret is required"
            );
        }

        if (!passwordEncoder.matches(
                clientSecret,
                payment.getClientSecretHash()
        )) {
            throw new UnauthorizedException(
                    "Invalid Client Secret"
            );
        }
    }
}