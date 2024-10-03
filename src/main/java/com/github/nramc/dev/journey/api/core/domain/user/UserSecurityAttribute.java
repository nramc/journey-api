package com.github.nramc.dev.journey.api.core.domain.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.github.nramc.dev.journey.api.core.utils.EmailAddressObfuscator;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDate;

@Builder(toBuilder = true)
public record UserSecurityAttribute(
        UserSecurityAttributeType type,
        String value,
        boolean enabled,
        boolean verified,
        @NotNull @JsonFormat(pattern = "yyyy-MM-dd") LocalDate creationDate,
        @NotNull @JsonFormat(pattern = "yyyy-MM-dd") LocalDate lastUpdateDate
) {
    public UserSecurityAttribute obfuscateSensitiveInformation() {
        return UserSecurityAttribute.builder()
                .type(type)
                .value(getObfuscatedValue())
                .enabled(enabled)
                .verified(verified)
                .creationDate(creationDate)
                .lastUpdateDate(lastUpdateDate)
                .build();
    }

    private String getObfuscatedValue() {
        return switch (type) {
            case EMAIL_ADDRESS -> EmailAddressObfuscator.obfuscate(value);
            case TOTP -> "***";
        };
    }
}
