package com.github.nramc.dev.journey.api.web.resources.rest.account.security;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;

public record EmailVerificationRequest(@JsonProperty("code") @NotBlank @Digits(integer = 6, fraction = 0) String code) {
}
