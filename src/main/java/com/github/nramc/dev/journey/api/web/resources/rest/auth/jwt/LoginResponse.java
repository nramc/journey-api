package com.github.nramc.dev.journey.api.web.resources.rest.auth.jwt;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import java.time.Instant;
import java.util.Set;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public record LoginResponse(
        String token,
        Instant expiredAt,
        Set<String> authorities
) {
}
