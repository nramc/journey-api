package com.github.nramc.dev.journey.api.infrastructure.ratelimit;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class RateLimitKeyResolver {
    private static final String CLOUDFLARE_CONNECTING_IP_HEADER = "CF-Connecting-IP";

    public String resolve(RateLimitKey keyType, HttpServletRequest request) {
        return switch (keyType) {
            case USERNAME -> resolveAccountKey(SecurityContextHolder.getContext().getAuthentication());
            case CLIENT_IP -> resolveClientIp(request);
        };
    }

    private String resolveClientIp(HttpServletRequest request) {
        String cloudflareIp = request.getHeader(CLOUDFLARE_CONNECTING_IP_HEADER);
        if (cloudflareIp != null && !cloudflareIp.isBlank()) {
            return cloudflareIp.trim();
        }

        String remoteAddress = request.getRemoteAddr();
        return remoteAddress == null || remoteAddress.isBlank() ? "unknown" : remoteAddress;
    }

    private String resolveAccountKey(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "anonymous";
        }

        String principal = authentication.getName();
        return principal == null || principal.isBlank() ? "anonymous" : principal;
    }

}
