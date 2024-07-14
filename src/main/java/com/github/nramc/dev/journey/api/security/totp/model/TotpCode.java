package com.github.nramc.dev.journey.api.security.totp.model;

public record TotpCode(String code) {
    public static TotpCode valueOf(String code) {
        return new TotpCode(code);
    }
}
