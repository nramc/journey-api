package com.github.nramc.dev.journey.api.shared.domain.user.security;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.UUID;
import org.jspecify.annotations.NonNull;

// todo: should go inside user.security
public record EmailToken(@UUID @NotBlank String token) {

    public static EmailToken valueOf(String token) {
        return new EmailToken(token);
    }

    @Override
    public @NonNull String toString() {
        return "EmailToken{token='***'}";
    }
}
