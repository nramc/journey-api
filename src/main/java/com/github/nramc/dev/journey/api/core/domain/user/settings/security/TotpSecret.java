package com.github.nramc.dev.journey.api.core.domain.user.settings.security;

public record TotpSecret(String secret) {

    public static TotpSecret valueOf(String secret) {
        return new TotpSecret(secret);
    }

    @Override
    public String toString() {
        return "TotpSecret{secret=***}";
    }
}
