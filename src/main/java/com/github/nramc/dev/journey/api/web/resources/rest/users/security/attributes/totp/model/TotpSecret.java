package com.github.nramc.dev.journey.api.web.resources.rest.users.security.attributes.totp.model;

public record TotpSecret(String secret) {

    public static TotpSecret valueOf(String secret) {
        return new TotpSecret(secret);
    }

    @Override
    public String toString() {
        return "TotpSecret{secret=***}";
    }
}
