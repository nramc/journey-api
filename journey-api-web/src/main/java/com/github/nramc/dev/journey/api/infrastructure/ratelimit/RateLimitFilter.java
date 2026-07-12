package com.github.nramc.dev.journey.api.infrastructure.ratelimit;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
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
    private final Map<String, RateLimitProperties.Policy> policies;

    public RateLimitFilter(RateLimiterService rateLimiterService,
                           RateLimitKeyResolver rateLimitKeyResolver,
                           JsonMapper jsonMapper,
                           RateLimitProperties properties) {
        this.rateLimiterService = rateLimiterService;
        this.rateLimitKeyResolver = rateLimitKeyResolver;
        this.jsonMapper = jsonMapper;
        this.policies = new LinkedHashMap<>();
        properties.policies().forEach(policy ->
                policies.put(policy.method().name() + " " + policy.path(), policy)
        );
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        String requestPath = normalizePath(request);
        String requestMethod = request.getMethod();
        RateLimitProperties.Policy rateLimitPolicy = policies.get(requestMethod + " " + requestPath);
        if (rateLimitPolicy == null) {
            filterChain.doFilter(request, response);
            return;
        }

        RateLimiterService.RateLimitDecision decision;
        try {
            String key = rateLimitKeyResolver.resolve(rateLimitPolicy.key(), request);
            decision = rateLimiterService.tryConsume(rateLimitPolicy, key);
        } catch (RuntimeException ex) {
            // fail-open: a bug/misconfiguration in the rate limiter must never block legitimate traffic
            log.warn("Rate limiting check failed for policy '{}', allowing request through", rateLimitPolicy.name(), ex);
            filterChain.doFilter(request, response);
            return;
        }

        if (!decision.allowed()) {
            writeTooManyRequestsResponse(response, decision.retryAfterSeconds(), rateLimitPolicy.name());
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
}
