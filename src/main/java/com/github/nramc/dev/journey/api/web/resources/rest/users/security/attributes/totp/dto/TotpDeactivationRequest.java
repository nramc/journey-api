package com.github.nramc.dev.journey.api.web.resources.rest.users.security.attributes.totp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record TotpDeactivationRequest(@NotBlank @Pattern(regexp = "\\d{6}") String code) {

    @Override
    public String toString() {
        return "TotpDeactivationRequest{code='***'}";
    }

}
