//package com.moddynerd.transiq.apikey.security;
//
//import org.springframework.security.authentication.AbstractAuthenticationToken;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//
//import java.util.List;
//
//public class ApiKeyAuthenticationToken extends AbstractAuthenticationToken {
//
//    private final Object principal;
//    private final String credentials;
//
//    public ApiKeyAuthenticationToken(String apiKey) {
//        super(List.of());
//        this.principal = null;
//        this.credentials = apiKey;
//        setAuthenticated(false);
//    }
//
//    public ApiKeyAuthenticationToken(ApiKeyPrincipal principal) {
//        super(List.of(new SimpleGrantedAuthority("ROLE_API")));
//        this.principal = principal;
//        this.credentials = null;
//        setAuthenticated(true);
//    }
//
//    @Override
//    public Object getCredentials() {
//        return credentials;
//    }
//
//    @Override
//    public Object getPrincipal() {
//        return principal;
//    }
//}