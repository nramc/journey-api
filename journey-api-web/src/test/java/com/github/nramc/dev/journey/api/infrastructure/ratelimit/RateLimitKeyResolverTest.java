package com.github.nramc.dev.journey.api.infrastructure.ratelimit;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.assertj.core.api.Assertions.assertThat;

class RateLimitKeyResolverTest {

    @Test
    void shouldUseCloudflareHeaderWhenPresent() {
        RateLimitKeyResolver resolver = new RateLimitKeyResolver();
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("CF-Connecting-IP", "1.2.3.4");
        request.setRemoteAddr("10.0.0.1");

        assertThat(resolver.resolveClientIp(request)).isEqualTo("1.2.3.4");
    }

    @Test
    void shouldFallBackToRemoteAddressWhenHeaderMissing() {
        RateLimitKeyResolver resolver = new RateLimitKeyResolver();
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr("10.0.0.1");

        assertThat(resolver.resolveClientIp(request)).isEqualTo("10.0.0.1");
    }

    @Test
    void shouldResolveAnonymousForUnauthenticatedUser() {
        RateLimitKeyResolver resolver = new RateLimitKeyResolver();
        assertThat(resolver.resolveAccountKey(null)).isEqualTo("anonymous");
    }
}
