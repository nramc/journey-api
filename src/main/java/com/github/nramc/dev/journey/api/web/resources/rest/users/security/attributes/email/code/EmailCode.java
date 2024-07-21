package com.github.nramc.dev.journey.api.web.resources.rest.users.security.attributes.email.code;

import com.github.nramc.dev.journey.api.web.resources.rest.users.security.confirmationcode.ConfirmationCode;
import jakarta.validation.constraints.NotBlank;

public record EmailCode(@NotBlank String code) implements ConfirmationCode {

    public static EmailCode valueOf(int code) {
        return new EmailCode(String.valueOf(code));
    }

    @Override
    public String toString() {
        return "EmailCode{code='***'}";
    }

    @Override
    public String getConfirmationCode() {
        return code;
    }
}
