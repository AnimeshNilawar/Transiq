//package com.moddynerd.transiq.apikey.security;
//
//import com.moddynerd.transiq.apikey.entity.ApiKey;
//import com.moddynerd.transiq.apikey.entity.ApiKeyStatus;
//import com.moddynerd.transiq.apikey.repository.ApiKeyRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.authentication.AuthenticationProvider;
//import org.springframework.security.authentication.BadCredentialsException;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Component;
//
//@Component
//@RequiredArgsConstructor
//public class ApiKeyAuthenticationProvider
//        implements AuthenticationProvider {
//
//    private final ApiKeyRepository apiKeyRepository;
//    private final PasswordEncoder passwordEncoder;
//
//    @Override
//    public Authentication authenticate(Authentication authentication) {
//        String apiKey = (String) authentication.getCredentials();
//
//        if (apiKey.length() < 16) {
//            throw new BadCredentialsException("Invalid API Key");
//        }
//
//        String prefix = apiKey.substring(0,16);
//
//        ApiKey key = apiKeyRepository.findByKeyPrefixAndStatus(prefix, ApiKeyStatus.ACTIVE)
//                .orElseThrow(() -> new BadCredentialsException("Invalid API Key"));
//
//        if(!passwordEncoder.matches(apiKey, key.getKeyHash())) {
//            throw new BadCredentialsException("Invalid API Key");
//        }
//
//        return new ApiKeyAuthenticationToken(
//                new ApiKeyPrincipal(key.getMerchant())
//        );
//    }
//
//    @Override
//    public boolean supports(Class<?> authentication) {
//
//        return ApiKeyAuthenticationToken.class.isAssignableFrom(authentication);
//    }
//}