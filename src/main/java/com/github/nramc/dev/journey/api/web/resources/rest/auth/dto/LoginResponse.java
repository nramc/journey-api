package com.github.nramc.dev.journey.api.web.resources.rest.auth.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.github.nramc.dev.journey.api.core.domain.user.UserSecurityAttributeType;
import lombok.Builder;

import java.time.Instant;
import java.util.Set;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@Builder(toBuilder = true)
public record LoginResponse(
        String token,
        Instant expiredAt,
        Set<String> authorities,
        String name,
        boolean additionalFactorRequired,
        Set<UserSecurityAttributeType> securityAttributes) {
}
