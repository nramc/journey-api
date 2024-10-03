package com.github.nramc.dev.journey.api.web.resources.rest.users.security.utils;

import com.github.nramc.dev.journey.api.core.domain.user.UserSecurityAttributeType;
import com.github.nramc.dev.journey.api.repository.user.AuthUser;
import com.github.nramc.dev.journey.api.repository.user.UserSecurityAttributeEntity;
import lombok.experimental.UtilityClass;

import java.time.LocalDate;

@UtilityClass
public class SecurityAttributesUtils {
    public static UserSecurityAttributeEntity newEmailAttribute(AuthUser authUser) {
        return UserSecurityAttributeEntity.builder()
                .type(UserSecurityAttributeType.EMAIL_ADDRESS)
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
                .type(UserSecurityAttributeType.TOTP)
                .userId(authUser.getId().toHexString())
                .username(authUser.getUsername())
                .creationDate(LocalDate.now())
                .lastUpdateDate(LocalDate.now())
                .enabled(true)
                .verified(false)
                .build();
    }
}
