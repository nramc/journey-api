package com.github.nramc.dev.journey.api.account.web.auth.dto;

import com.github.nramc.dev.journey.api.shared.domain.user.security.UserSecurityAttributeType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record MultiFactorAuthenticationRequest(
        @NotNull UserSecurityAttributeType type,
        @NotBlank String value) {
}
