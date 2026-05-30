package com.github.nramc.dev.journey.api.account.codes;

public record TotpCode(String code) implements ConfirmationCode {
    public static TotpCode valueOf(String code) {
        return new TotpCode(code);
    }

}
