package com.github.nramc.dev.journey.api.web.dto.user.security;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.github.nramc.dev.journey.api.core.domain.user.UserSecurityAttributeType;
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
}
