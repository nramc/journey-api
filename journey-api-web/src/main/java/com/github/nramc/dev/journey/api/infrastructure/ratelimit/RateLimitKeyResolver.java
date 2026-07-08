package com.github.nramc.dev.journey.api.infrastructure.ratelimit;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;

public class RateLimitKeyResolver {
    private static final String CLOUDFLARE_CONNECTING_IP_HEADER = "CF-Connecting-IP";

    public String resolveClientIp(HttpServletRequest request) {
        String cloudflareIp = request.getHeader(CLOUDFLARE_CONNECTING_IP_HEADER);
        if (cloudflareIp != null && !cloudflareIp.isBlank()) {
            return cloudflareIp.trim();
        }

        String remoteAddress = request.getRemoteAddr();
        return remoteAddress == null || remoteAddress.isBlank() ? "unknown" : remoteAddress;
    }

    public String resolveAccountKey(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "anonymous";
        }

        String principal = authentication.getName();
        return principal == null || principal.isBlank() ? "anonymous" : principal;
    }

}
