package com.github.nramc.dev.journey.api.web.resources.rest.users.security.utils;

import com.github.nramc.dev.journey.api.core.security.attributes.SecurityAttributeType;
import com.github.nramc.dev.journey.api.repository.auth.AuthUser;
import com.github.nramc.dev.journey.api.repository.auth.UserSecurityAttributeEntity;
import lombok.experimental.UtilityClass;

import java.time.LocalDate;

@UtilityClass
public class SecurityAttributesUtils {
    public static UserSecurityAttributeEntity newEmailAttribute(AuthUser authUser) {
        return UserSecurityAttributeEntity.builder()
                .type(SecurityAttributeType.EMAIL_ADDRESS)
                .userId(authUser.getId().toHexString())
                .username(authUser.getUsername())
                .creationDate(LocalDate.now())
                .lastUpdateDate(LocalDate.now())
                .enabled(true)
                .verified(false)
                .build();
    }

    public static UserSecurityAttributeEntity newTotpAttribute(AuthUser authUser) {
        return UserSecurityAttributeEntity.builder()
                .type(SecurityAttributeType.TOTP)
                .userId(authUser.getId().toHexString())
                .username(authUser.getUsername())
                .creationDate(LocalDate.now())
                .lastUpdateDate(LocalDate.now())
                .enabled(true)
                .verified(false)
                .build();
    }
}
