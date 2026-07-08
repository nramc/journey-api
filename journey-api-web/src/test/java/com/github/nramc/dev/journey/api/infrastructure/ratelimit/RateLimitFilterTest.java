package com.github.nramc.dev.journey.api.infrastructure.ratelimit;

import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import tools.jackson.databind.json.JsonMapper;

import java.time.Duration;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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

    private static @NonNull RateLimitFilter getRateLimitFilter() {
        RateLimitProperties.Policy exhaustedPolicy = new RateLimitProperties.Policy(1, Duration.ofMinutes(1));
        RateLimitProperties.Policy defaultPolicy = new RateLimitProperties.Policy(100, Duration.ofMinutes(1));

        RateLimitProperties properties = new RateLimitProperties(Map.of(
                "journey-creation", exhaustedPolicy,
                "login", defaultPolicy,
                "ott-login", defaultPolicy,
                "mfa-verify", defaultPolicy,
                "account-recovery", defaultPolicy
        ));

        RateLimiterService rateLimiterService = new RateLimiterService(properties);
        return new RateLimitFilter(
                rateLimiterService,
                new RateLimitKeyResolver(),
                new JsonMapper()
        );
    }

    @Test
    void shouldFailFastAtConstructionWhenAPolicyIsMissing() {
        // only "login" is configured; the other rules (ott-login, mfa-verify, account-recovery,
        // journey-creation) reference policies that don't exist and must be caught eagerly.
        RateLimitProperties.Policy policy = new RateLimitProperties.Policy(5, Duration.ofMinutes(1));
        RateLimitProperties properties = new RateLimitProperties(Map.of("login", policy));

        RateLimiterService rateLimiterService = new RateLimiterService(properties);

        var rateLimitKeyResolver = new RateLimitKeyResolver();
        var jsonMapper = new JsonMapper();
        assertThatThrownBy(() -> new RateLimitFilter(rateLimiterService, rateLimitKeyResolver, jsonMapper)).isInstanceOf(IllegalArgumentException.class);
    }
}
