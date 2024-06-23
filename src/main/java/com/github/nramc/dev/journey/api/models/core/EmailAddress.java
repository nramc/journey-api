package com.github.nramc.dev.journey.api.models.core;

import com.github.nramc.dev.journey.api.utils.EmailAddressObfuscator;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record EmailAddress(@NotBlank @Email String value) {

    public static EmailAddress valueOf(String value) {
        return new EmailAddress(value);
    }

    @Override
    public String toString() {
        return "EmailAddress{" + "value='" + EmailAddressObfuscator.obfuscate(value) + '\'' + '}';
    }
}
