package com.github.nramc.dev.journey.api.services.email;

import com.github.nramc.dev.journey.api.services.confirmationcode.ConfirmationCode;
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
