package com.github.nramc.dev.journey.api.core.usecase.code;

import jakarta.validation.constraints.NotBlank;

public record EmailCode(@NotBlank String code) implements ConfirmationCode {

    public static EmailCode valueOf(int code) {
        return new EmailCode(String.valueOf(code));
    }

    @Override
    public String toString() {
        return "EmailCode{code='***'}";
    }

}
