package com.github.nramc.dev.journey.api.infrastructure.ratelimit;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.assertj.core.api.Assertions.assertThat;

class RateLimitKeyResolverTest {

    @AfterEach
    void cleanupSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldUseCloudflareHeaderWhenPresent() {
        RateLimitKeyResolver resolver = new RateLimitKeyResolver();
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("CF-Connecting-IP", "1.2.3.4");
        request.setRemoteAddr("10.0.0.1");

        assertThat(resolver.resolve(RateLimitKey.CLIENT_IP, request)).isEqualTo("1.2.3.4");
    }

    @Test
    void shouldFallBackToRemoteAddressWhenHeaderMissing() {
        RateLimitKeyResolver resolver = new RateLimitKeyResolver();
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr("10.0.0.1");

        assertThat(resolver.resolve(RateLimitKey.CLIENT_IP, request)).isEqualTo("10.0.0.1");
    }

    @Test
    void shouldFallBackToRemoteAddressWhenCloudflareHeaderIsBlank() {
        RateLimitKeyResolver resolver = new RateLimitKeyResolver();
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("CF-Connecting-IP", "   ");
        request.setRemoteAddr("10.0.0.1");

        assertThat(resolver.resolve(RateLimitKey.CLIENT_IP, request)).isEqualTo("10.0.0.1");
    }

    @Test
    void shouldReturnUnknownWhenRemoteAddressIsBlank() {
        RateLimitKeyResolver resolver = new RateLimitKeyResolver();
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr("   ");

        assertThat(resolver.resolve(RateLimitKey.CLIENT_IP, request)).isEqualTo("unknown");
    }

    @Test
    void shouldResolveAnonymousForUnauthenticatedUser() {
        RateLimitKeyResolver resolver = new RateLimitKeyResolver();
        MockHttpServletRequest request = new MockHttpServletRequest();

        assertThat(resolver.resolve(RateLimitKey.USERNAME, request)).isEqualTo("anonymous");
    }

    @Test
    void shouldResolveUsernameForAuthenticatedUser() {
        RateLimitKeyResolver resolver = new RateLimitKeyResolver();
        SecurityContextHolder.getContext().setAuthentication(
                new TestingAuthenticationToken("user-1", "password", "AUTHENTICATED_USER")
        );
        MockHttpServletRequest request = new MockHttpServletRequest();

        assertThat(resolver.resolve(RateLimitKey.USERNAME, request)).isEqualTo("user-1");
    }

    @Test
    void shouldResolveAnonymousWhenPrincipalIsBlank() {
        RateLimitKeyResolver resolver = new RateLimitKeyResolver();
        SecurityContextHolder.getContext().setAuthentication(
                new TestingAuthenticationToken("   ", "password", "AUTHENTICATED_USER")
        );
        MockHttpServletRequest request = new MockHttpServletRequest();

        assertThat(resolver.resolve(RateLimitKey.USERNAME, request)).isEqualTo("anonymous");
    }
}
