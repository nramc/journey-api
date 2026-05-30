package com.github.nramc.dev.journey.api.account.web.users.security.attributes.totp;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record VerifyTotpCodeRequest(@NotBlank @Pattern(regexp = "\\d{6}") String code) {
    @Override
    public String toString() {
        return "VerifyTotpCode{code='***'}";
    }
}
