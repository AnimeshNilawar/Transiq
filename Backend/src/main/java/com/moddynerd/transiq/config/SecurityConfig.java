package com.moddynerd.transiq.config;

import com.moddynerd.transiq.apikey.security.ApiKeyAuthenticationEntryPoint;
import com.moddynerd.transiq.apikey.security.ApiKeyAuthenticationFilter;
import com.moddynerd.transiq.apikey.security.ApiKeyScopeFilter;
import com.moddynerd.transiq.apikey.service.ApiKeyAuthenticationService;
import com.moddynerd.transiq.auth.security.CustomUserDetailsService;
import com.moddynerd.transiq.auth.security.JwtAuthenticationFilter;
import com.moddynerd.transiq.auth.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthenticationProvider authenticationProvider;
    private final ApiKeyAuthenticationEntryPoint apiKeyAuthenticationEntryPoint;
    private final JwtService jwtService;
    private final CustomUserDetailsService customUserDetailsService;
    private final ApiKeyAuthenticationService apiKeyAuthenticationService;

    @Value("${cors.allowed-origins:http://localhost:3000}")
    private String allowedOrigins;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of(allowedOrigins.split(",")));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", config);
        return source;
    }

    private static final java.util.Set<String> ALLOWED_SORT_PROPERTIES = java.util.Set.of(
            "createdAt", "updatedAt", "amount", "status", "id"
    );

    @Bean
    @Order(0)
    public SecurityFilterChain actuatorSecurityFilterChain(
            HttpSecurity http
    ) throws Exception {
        http
                .securityMatcher("/actuator/**", "/api/v1/merchants/register")
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/actuator/health", "/actuator/info").permitAll()
                        .anyRequest().permitAll()
                );
        return http.build();
    }

    @Bean
    @Order(1)
    public SecurityFilterChain authSecurityFilterChain(
            HttpSecurity http
    ) throws Exception {

        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .securityMatcher("/api/v1/auth/**")

                .csrf(csrf -> csrf.disable())

                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()
                )

                .authenticationProvider(authenticationProvider)

                .addFilterBefore(
                        new JwtAuthenticationFilter(jwtService, customUserDetailsService),
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain adminSecurityFilterChain(
            HttpSecurity http
    ) throws Exception {

        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .securityMatcher("/api/v1/admin/**")
                .csrf(csrf -> csrf.disable())

                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                .authorizeHttpRequests(auth -> auth
                        .anyRequest().authenticated()
                )

                .addFilterBefore(
                        new RateLimitingFilter(),
                        UsernamePasswordAuthenticationFilter.class
                )
                .addFilterBefore(
                        new JwtAuthenticationFilter(jwtService, customUserDetailsService),
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

    @Bean
    @Order(3)
    public SecurityFilterChain paymentSecurityFilterChain(
            HttpSecurity http
    ) throws Exception {

        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .securityMatcher((jakarta.servlet.http.HttpServletRequest request) -> {
                    String path = request.getServletPath();
                    if (path.startsWith("/api/v1/dashboard")) {
                        return false;
                    }
                    return path.startsWith("/api/v1/payments")
                            || path.startsWith("/api/v1/ledger")
                            || path.startsWith("/api/v1/settlements")
                            || path.startsWith("/api/v1/refunds")
                            || path.startsWith("/api/v1/webhooks")
                            || path.startsWith("/api/v1/chargebacks")
                            || path.startsWith("/api/v1/adjustments");
                })

                .csrf(csrf -> csrf.disable())

                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )
                .exceptionHandling(exception -> exception.authenticationEntryPoint(apiKeyAuthenticationEntryPoint))

                .authorizeHttpRequests(auth -> auth
                        .anyRequest().authenticated()
                )

                .addFilterBefore(
                        new RateLimitingFilter(),
                        UsernamePasswordAuthenticationFilter.class
                )
                .addFilterBefore(
                        new ApiKeyAuthenticationFilter(apiKeyAuthenticationService),
                        UsernamePasswordAuthenticationFilter.class
                )
                .addFilterAfter(
                        new ApiKeyScopeFilter(),
                        ApiKeyAuthenticationFilter.class
                );

        return http.build();
    }

    @Bean
    @Order(4)
    public SecurityFilterChain dashboardSecurityFilterChain(
            HttpSecurity http
    ) throws Exception {

        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .securityMatcher("/api/v1/dashboard/**")
                .csrf(csrf -> csrf.disable())

                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                .authorizeHttpRequests(auth -> auth
                        .anyRequest().authenticated()
                )

                .addFilterBefore(
                        new RateLimitingFilter(),
                        UsernamePasswordAuthenticationFilter.class
                )
                .addFilterBefore(
                        new JwtAuthenticationFilter(jwtService, customUserDetailsService),
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

    @Bean
    @Order(5)
    public SecurityFilterChain defaultSecurityFilterChain(
            HttpSecurity http
    ) throws Exception {

        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())

                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                .authorizeHttpRequests(auth -> auth
                        .anyRequest().authenticated()
                )

                .addFilterBefore(
                        new RateLimitingFilter(),
                        UsernamePasswordAuthenticationFilter.class
                )
                .addFilterBefore(
                        new JwtAuthenticationFilter(jwtService, customUserDetailsService),
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}