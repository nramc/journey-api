package com.github.nramc.dev.journey.api.core.usecase.code;

public record TotpCode(String code) implements ConfirmationCode {
    public static TotpCode valueOf(String code) {
        return new TotpCode(code);
    }

}
