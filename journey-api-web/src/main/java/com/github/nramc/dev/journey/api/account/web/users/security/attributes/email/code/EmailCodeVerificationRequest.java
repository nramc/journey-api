package com.github.nramc.dev.journey.api.account.web.users.security.attributes.email.code;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;

public record EmailCodeVerificationRequest(@JsonProperty("code") @NotBlank @Digits(integer = 6, fraction = 0) String code) {
}
