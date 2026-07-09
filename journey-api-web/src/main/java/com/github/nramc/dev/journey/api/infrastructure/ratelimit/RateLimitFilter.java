package com.github.nramc.dev.journey.api.infrastructure.ratelimit;

import com.github.nramc.dev.journey.api.shared.web.Resources;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
public class RateLimitFilter extends OncePerRequestFilter {
    private final RateLimiterService rateLimiterService;
    private final RateLimitKeyResolver rateLimitKeyResolver;
    private final JsonMapper jsonMapper;
    private final Map<String, RateLimitRule> rules;

    public RateLimitFilter(RateLimiterService rateLimiterService,
                           RateLimitKeyResolver rateLimitKeyResolver,
                           JsonMapper jsonMapper) {
        this.rateLimiterService = rateLimiterService;
        this.rateLimitKeyResolver = rateLimitKeyResolver;
        this.jsonMapper = jsonMapper;
        this.rules = new LinkedHashMap<>();
        registerRule(HttpMethod.POST.name(), Resources.LOGIN, "login", false);
        registerRule(HttpMethod.POST.name(), Resources.LOGIN_OTT, "ott-login", false);
        registerRule(HttpMethod.POST.name(), Resources.LOGIN_MFA, "mfa-verify", false);
        registerRule(HttpMethod.POST.name(), Resources.SEND_ACCOUNT_RECOVERY, "account-recovery", false);
        registerRule(HttpMethod.POST.name(), Resources.NEW_JOURNEY, "journey-creation", true);

        // fail fast: verify every referenced policy is actually configured, instead of failing per-request
        this.rules.values().forEach(rule -> rateLimiterService.assertPolicyConfigured(rule.policyName()));
    }

    private void registerRule(String method, String path, String policyName, boolean accountBased) {
        rules.put(method + " " + path, new RateLimitRule(policyName, accountBased));
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        String requestPath = normalizePath(request);
        String requestMethod = request.getMethod();
        RateLimitRule rateLimitRule = rules.get(requestMethod + " " + requestPath);
        if (rateLimitRule == null) {
            filterChain.doFilter(request, response);
            return;
        }

        RateLimiterService.RateLimitDecision decision;
        try {
            String key = rateLimitRule.accountBased()
                    ? rateLimitKeyResolver.resolveAccountKey(SecurityContextHolder.getContext().getAuthentication())
                    : rateLimitKeyResolver.resolveClientIp(request);
            decision = rateLimiterService.tryConsume(rateLimitRule.policyName(), key);
        } catch (RuntimeException ex) {
            // fail-open: a bug/misconfiguration in the rate limiter must never block legitimate traffic
            log.warn("Rate limiting check failed for policy '{}', allowing request through", rateLimitRule.policyName(), ex);
            filterChain.doFilter(request, response);
            return;
        }

        if (!decision.allowed()) {
            writeTooManyRequestsResponse(response, decision.retryAfterSeconds(), rateLimitRule.policyName());
            return;
        }

        filterChain.doFilter(request, response);
    }

    private String normalizePath(HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        String contextPath = request.getContextPath();
        if (contextPath != null && !contextPath.isBlank() && requestUri.startsWith(contextPath)) {
            return requestUri.substring(contextPath.length());
        }
        return requestUri;
    }

    private void writeTooManyRequestsResponse(HttpServletResponse response,
                                              long retryAfterSeconds,
                                              String policyName) throws IOException {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.TOO_MANY_REQUESTS,
                "Rate limit exceeded for policy '%s'".formatted(policyName)
        );
        problemDetail.setTitle("Too Many Requests");
        problemDetail.setProperty("policy", policyName);

        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        response.setHeader(HttpHeaders.RETRY_AFTER, String.valueOf(retryAfterSeconds));
        response.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        jsonMapper.writeValue(response.getWriter(), problemDetail);
    }

    private record RateLimitRule(String policyName, boolean accountBased) {
    }
}
