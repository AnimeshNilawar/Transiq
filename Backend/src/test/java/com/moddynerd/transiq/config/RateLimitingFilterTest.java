package com.moddynerd.transiq.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class RateLimitingFilterTest {

    private RateLimitingFilter rateLimitingFilter;
    private FilterChain filterChain;

    @BeforeEach
    void setUp() {
        rateLimitingFilter = new RateLimitingFilter();
        filterChain = mock(FilterChain.class);
    }

    @Test
    void requestWithinLimit_passes() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/v1/payments");
        request.setRemoteAddr("192.168.1.1");
        MockHttpServletResponse response = new MockHttpServletResponse();

        rateLimitingFilter.doFilterInternal(request, response, filterChain);

        assertThat(response.getStatus()).isEqualTo(200);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void requestOverLimit_returns429() throws ServletException, IOException {
        for (int i = 0; i < 60; i++) {
            MockHttpServletRequest req = new MockHttpServletRequest("GET", "/api/v1/payments");
            req.setRemoteAddr("10.0.0.1");
            MockHttpServletResponse resp = new MockHttpServletResponse();
            rateLimitingFilter.doFilterInternal(req, resp, filterChain);
        }

        MockHttpServletRequest overLimitRequest = new MockHttpServletRequest("GET", "/api/v1/payments");
        overLimitRequest.setRemoteAddr("10.0.0.1");
        MockHttpServletResponse overLimitResponse = new MockHttpServletResponse();

        rateLimitingFilter.doFilterInternal(overLimitRequest, overLimitResponse, filterChain);

        assertThat(overLimitResponse.getStatus()).isEqualTo(429);
        assertThat(overLimitResponse.getContentAsString()).contains("Rate limit exceeded");
        verify(filterChain, never()).doFilter(overLimitRequest, overLimitResponse);
    }

    @Test
    void authEndpoint_hasLowerLimit() throws ServletException, IOException {
        for (int i = 0; i < 10; i++) {
            MockHttpServletRequest req = new MockHttpServletRequest("POST", "/api/v1/auth/login");
            req.setRemoteAddr("172.16.0.1");
            MockHttpServletResponse resp = new MockHttpServletResponse();
            rateLimitingFilter.doFilterInternal(req, resp, filterChain);
        }

        MockHttpServletRequest overLimitRequest = new MockHttpServletRequest("POST", "/api/v1/auth/login");
        overLimitRequest.setRemoteAddr("172.16.0.1");
        MockHttpServletResponse overLimitResponse = new MockHttpServletResponse();

        rateLimitingFilter.doFilterInternal(overLimitRequest, overLimitResponse, filterChain);

        assertThat(overLimitResponse.getStatus()).isEqualTo(429);
        verify(filterChain, never()).doFilter(overLimitRequest, overLimitResponse);
    }

    @Test
    void differentIps_haveSeparateLimits() throws ServletException, IOException {
        for (int i = 0; i < 60; i++) {
            MockHttpServletRequest req = new MockHttpServletRequest("GET", "/api/v1/payments");
            req.setRemoteAddr("192.168.1.100");
            MockHttpServletResponse resp = new MockHttpServletResponse();
            rateLimitingFilter.doFilterInternal(req, resp, filterChain);
        }

        MockHttpServletRequest differentIpRequest = new MockHttpServletRequest("GET", "/api/v1/payments");
        differentIpRequest.setRemoteAddr("192.168.1.200");
        MockHttpServletResponse differentIpResponse = new MockHttpServletResponse();

        rateLimitingFilter.doFilterInternal(differentIpRequest, differentIpResponse, filterChain);

        assertThat(differentIpResponse.getStatus()).isEqualTo(200);
        verify(filterChain).doFilter(differentIpRequest, differentIpResponse);
    }

    @Test
    void xForwardedFor_header_isUsedAsClientIp() throws ServletException, IOException {
        for (int i = 0; i < 60; i++) {
            MockHttpServletRequest req = new MockHttpServletRequest("GET", "/api/v1/payments");
            req.setRemoteAddr("127.0.0.1");
            req.addHeader("X-Forwarded-For", "203.0.113.50, 70.41.3.18");
            MockHttpServletResponse resp = new MockHttpServletResponse();
            rateLimitingFilter.doFilterInternal(req, resp, filterChain);
        }

        MockHttpServletRequest overLimitRequest = new MockHttpServletRequest("GET", "/api/v1/payments");
        overLimitRequest.setRemoteAddr("127.0.0.1");
        overLimitRequest.addHeader("X-Forwarded-For", "203.0.113.50, 70.41.3.18");
        MockHttpServletResponse overLimitResponse = new MockHttpServletResponse();

        rateLimitingFilter.doFilterInternal(overLimitRequest, overLimitResponse, filterChain);

        assertThat(overLimitResponse.getStatus()).isEqualTo(429);
    }

    @Test
    void authEndpoint_doesNotAffectOtherEndpointLimits() throws ServletException, IOException {
        for (int i = 0; i < 10; i++) {
            MockHttpServletRequest req = new MockHttpServletRequest("POST", "/api/v1/auth/login");
            req.setRemoteAddr("10.1.1.1");
            MockHttpServletResponse resp = new MockHttpServletResponse();
            rateLimitingFilter.doFilterInternal(req, resp, filterChain);
        }

        MockHttpServletRequest paymentRequest = new MockHttpServletRequest("GET", "/api/v1/payments");
        paymentRequest.setRemoteAddr("10.1.1.1");
        MockHttpServletResponse paymentResponse = new MockHttpServletResponse();

        rateLimitingFilter.doFilterInternal(paymentRequest, paymentResponse, filterChain);

        assertThat(paymentResponse.getStatus()).isEqualTo(200);
        verify(filterChain).doFilter(paymentRequest, paymentResponse);
    }

    @Test
    void rateLimitExceeded_responseContainsCorrectJson() throws ServletException, IOException {
        for (int i = 0; i < 60; i++) {
            MockHttpServletRequest req = new MockHttpServletRequest("GET", "/api/v1/payments");
            req.setRemoteAddr("10.2.2.2");
            MockHttpServletResponse resp = new MockHttpServletResponse();
            rateLimitingFilter.doFilterInternal(req, resp, filterChain);
        }

        MockHttpServletRequest overLimitRequest = new MockHttpServletRequest("GET", "/api/v1/payments");
        overLimitRequest.setRemoteAddr("10.2.2.2");
        MockHttpServletResponse overLimitResponse = new MockHttpServletResponse();

        rateLimitingFilter.doFilterInternal(overLimitRequest, overLimitResponse, filterChain);

        assertThat(overLimitResponse.getStatus()).isEqualTo(429);
        assertThat(overLimitResponse.getContentType()).isEqualTo("application/json");
        assertThat(overLimitResponse.getContentAsString()).contains("\"status\":429");
        assertThat(overLimitResponse.getContentAsString()).contains("Too Many Requests");
    }
}
