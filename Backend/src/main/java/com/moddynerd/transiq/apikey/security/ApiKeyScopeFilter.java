package com.moddynerd.transiq.apikey.security;


import com.moddynerd.transiq.apikey.entity.ApiKeyType;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

public class ApiKeyScopeFilter extends OncePerRequestFilter {

    private static final Set<String> PUBLISHABLE_PATHS = Set.of("/api/v1/payments");
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof ApiKeyPrincipal principal)) {
            filterChain.doFilter(request, response);
            return;
        }

        ApiKeyType keyType = principal.type();
        String method = request.getMethod();
        String path = request.getServletPath();

        if (keyType == ApiKeyType.SECRET) {
            filterChain.doFilter(request, response);
            return;
        }

        if (keyType == ApiKeyType.RESTRICTED && !"GET".equals(method)) {
            reject(response, "RESTRICTED keys are read-only. Only GET operations are allowed.");
            return;
        }

        if (keyType == ApiKeyType.PUBLISHABLE) {
            if (!isPublishableAllowed(method, path)) {
                reject(response, "PUBLISHABLE keys can only create payments (POST /payments) and retrieve payment details (GET /payments/{ref}).");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private boolean isPublishableAllowed(String method, String path) {
        if ("POST".equals(method) && path.equals("/api/v1/payments")) {
            return true;
        }
        if ("GET".equals(method) && path.matches("/api/v1/payments/[a-fA-F0-9\\-]+")) {
            return true;
        }
        return false;
    }

    private void reject(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getOutputStream(), Map.of(
                "status", 403,
                "error", "Forbidden",
                "message", message
        ));
    }
}
