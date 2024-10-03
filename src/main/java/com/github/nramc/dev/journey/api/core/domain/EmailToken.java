package com.github.nramc.dev.journey.api.core.domain;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.UUID;

public record EmailToken(@UUID @NotBlank String token) {

    public static EmailToken valueOf(String token) {
        return new EmailToken(token);
    }

    @Override
    public String toString() {
        return "EmailToken{token='***'}";
    }
}
