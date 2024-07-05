package com.github.nramc.dev.journey.api.web.resources.rest.users.security.utils;

import com.github.nramc.dev.journey.api.models.core.SecurityAttributeType;
import com.github.nramc.dev.journey.api.repository.auth.AuthUser;
import com.github.nramc.dev.journey.api.repository.auth.UserSecurityAttributesEntity;
import lombok.experimental.UtilityClass;

import java.time.LocalDate;

@UtilityClass
public class SecurityAttributesUtils {
    public static UserSecurityAttributesEntity newEmailAttribute(AuthUser authUser) {
        return UserSecurityAttributesEntity.builder()
                .type(SecurityAttributeType.EMAIL_ADDRESS)
                .userId(authUser.getId().toHexString())
                .username(authUser.getUsername())
                .creationDate(LocalDate.now())
                .lastUpdateDate(LocalDate.now())
                .enabled(true)
                .verified(false)
                .build();
    }
}
