package com.github.nramc.dev.journey.api.web.resources.rest.auth.dto;

import com.github.nramc.dev.journey.api.core.security.attributes.SecurityAttributeType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record MultiFactorAuthenticationRequest(
        @NotNull SecurityAttributeType type,
        @NotBlank String value) {
}
