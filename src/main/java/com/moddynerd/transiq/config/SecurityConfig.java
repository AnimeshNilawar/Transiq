package com.moddynerd.transiq.config;

import com.moddynerd.transiq.apikey.security.ApiKeyAuthenticationEntryPoint;
import com.moddynerd.transiq.apikey.security.ApiKeyAuthenticationFilter;
import com.moddynerd.transiq.auth.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final ApiKeyAuthenticationFilter apiKeyAuthenticationFilter;
    private final AuthenticationProvider authenticationProvider;
    private final ApiKeyAuthenticationEntryPoint apiKeyAuthenticationEntryPoint;

    @Bean
    @Order(1)
    public SecurityFilterChain authSecurityFilterChain(
            HttpSecurity http
    ) throws Exception {

        http
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
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain paymentSecurityFilterChain(
            HttpSecurity http
    ) throws Exception {

        http
                .securityMatcher("/api/v1/payments/**")

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
                        apiKeyAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

    @Bean
    @Order(3)
    public SecurityFilterChain defaultSecurityFilterChain(
            HttpSecurity http
    ) throws Exception {

        http
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
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}