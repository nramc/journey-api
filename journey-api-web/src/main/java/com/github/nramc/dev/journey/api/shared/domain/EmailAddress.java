package com.github.nramc.dev.journey.api.shared.domain;

import com.github.nramc.dev.journey.api.shared.utils.EmailAddressObfuscator;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.jspecify.annotations.NonNull;

public record EmailAddress(@NotBlank @Email String value) {

    public static EmailAddress valueOf(String value) {
        return new EmailAddress(value);
    }

    @Override
    public @NonNull String toString() {
        return "EmailAddress{" + "value='" + EmailAddressObfuscator.obfuscate(value) + '\'' + '}';
    }
}
