package com.github.nramc.dev.journey.api.web.resources.rest.auth.dto;

import com.github.nramc.dev.journey.api.core.domain.user.UserSecurityAttributeType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record MultiFactorAuthenticationRequest(
        @NotNull UserSecurityAttributeType type,
        @NotBlank String value) {
}
