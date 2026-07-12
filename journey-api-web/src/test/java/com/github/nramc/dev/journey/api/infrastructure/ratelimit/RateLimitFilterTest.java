package com.github.nramc.dev.journey.api.infrastructure.ratelimit;

import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import tools.jackson.databind.json.JsonMapper;

import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class RateLimitFilterTest {
    @AfterEach
    void cleanupSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldReturn429WhenJourneyCreationLimitIsExceeded() throws Exception {
        var filter = getRateLimitFilter();

        SecurityContextHolder.getContext().setAuthentication(
                new TestingAuthenticationToken("user-1", "password", "AUTHENTICATED_USER")
        );

        MockHttpServletRequest firstRequest = new MockHttpServletRequest("POST", "/rest/journey");
        MockHttpServletResponse firstResponse = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        filter.doFilter(firstRequest, firstResponse, chain);
        filter.doFilter(firstRequest, firstResponse, chain);

        assertThat(firstResponse.getStatus()).isEqualTo(429);
        assertThat(firstResponse.getHeader(HttpHeaders.RETRY_AFTER)).isNotBlank();
        assertThat(firstResponse.getContentType()).contains("application/problem+json");
    }

    @Test
    void shouldRateLimitByClientIpForLoginEndpoint() throws Exception {
        var filter = getRateLimitFilter();

        MockHttpServletRequest firstRequest = new MockHttpServletRequest("POST", "/rest/login");
        firstRequest.setRemoteAddr("192.168.1.1");
        MockHttpServletResponse firstResponse = new MockHttpServletResponse();
        MockFilterChain firstChain = new MockFilterChain();

        filter.doFilter(firstRequest, firstResponse, firstChain);

        MockHttpServletResponse secondResponse = new MockHttpServletResponse();
        MockFilterChain secondChain = new MockFilterChain();
        filter.doFilter(firstRequest, secondResponse, secondChain);

        assertThat(secondResponse.getStatus()).isEqualTo(429);
    }

    @Test
    void shouldStripContextPathWhenMatchingPolicy() throws Exception {
        var filter = getRateLimitFilter();

        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/rest/login");
        request.setContextPath("/api");
        request.setRemoteAddr("192.168.1.1");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        filter.doFilter(request, response, chain);

        assertThat(response.getStatus()).isEqualTo(200);
    }

    @Test
    void shouldPassThroughWhenNoPolicyMatches() throws Exception {
        var filter = getRateLimitFilter();

        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/rest/health");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        filter.doFilter(request, response, chain);

        assertThat(response.getStatus()).isEqualTo(200);
    }

    @Test
    void shouldFailOpenWhenRateLimiterThrows() throws Exception {
        RateLimitKeyResolver keyResolver = mock(RateLimitKeyResolver.class);
        doThrow(new RuntimeException("resolver failure")).when(keyResolver).resolve(any(), any());

        RateLimitProperties properties = new RateLimitProperties(List.of(
                new RateLimitProperties.Policy("login", HttpMethod.POST, "/rest/login", 1, Duration.ofMinutes(1), RateLimitKey.CLIENT_IP)
        ));

        var filter = new RateLimitFilter(new RateLimiterService(), keyResolver, new JsonMapper(), properties);

        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/rest/login");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        filter.doFilter(request, response, chain);

        assertThat(response.getStatus()).isEqualTo(200);
        verify(keyResolver).resolve(any(), any());
    }

    private static @NonNull RateLimitFilter getRateLimitFilter() {
        RateLimitProperties properties = new RateLimitProperties(List.of(
                new RateLimitProperties.Policy("login", HttpMethod.POST, "/rest/login", 1, Duration.ofMinutes(1), RateLimitKey.CLIENT_IP),
                new RateLimitProperties.Policy("ott-login", HttpMethod.POST, "/rest/login/ott", 100, Duration.ofMinutes(1), RateLimitKey.CLIENT_IP),
                new RateLimitProperties.Policy("mfa-verify", HttpMethod.POST, "/rest/mfa", 100, Duration.ofMinutes(1), RateLimitKey.CLIENT_IP),
                new RateLimitProperties.Policy("account-recovery", HttpMethod.POST, "/rest/account/recover", 100, Duration.ofMinutes(1), RateLimitKey.CLIENT_IP),
                new RateLimitProperties.Policy("journey-creation", HttpMethod.POST, "/rest/journey", 1, Duration.ofMinutes(1), RateLimitKey.USERNAME)
        ));

        RateLimiterService rateLimiterService = new RateLimiterService();
        return new RateLimitFilter(
                rateLimiterService,
                new RateLimitKeyResolver(),
                new JsonMapper(),
                properties
        );
    }
}
