package com.github.nramc.dev.journey.api.web.resources.rest.users.security.totp;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record TotpActivationRequest(
        @NotBlank String secretKey,
        @NotBlank @Pattern(regexp = "\\d{6}") String code
) {
    @Override
    public String toString() {
        return "TotpActivationRequest{secretKey='***', code='***'}";
    }
}
