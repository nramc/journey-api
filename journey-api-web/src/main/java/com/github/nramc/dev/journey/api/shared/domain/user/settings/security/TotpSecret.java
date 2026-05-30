package com.github.nramc.dev.journey.api.shared.domain.user.settings.security;

import org.jspecify.annotations.NonNull;

// todo: should go inside user.security
public record TotpSecret(String secret) {

    public static TotpSecret valueOf(String secret) {
        return new TotpSecret(secret);
    }

    @Override
    public @NonNull String toString() {
        return "TotpSecret{secret=***}";
    }
}
