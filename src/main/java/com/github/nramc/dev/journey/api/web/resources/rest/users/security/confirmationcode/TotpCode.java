package com.github.nramc.dev.journey.api.web.resources.rest.users.security.confirmationcode;

public record TotpCode(String code) implements ConfirmationCode {
    public static TotpCode valueOf(String code) {
        return new TotpCode(code);
    }

}
