package com.github.nramc.dev.journey.api.web.dto.user.security;

import com.github.nramc.dev.journey.api.repository.auth.UserSecurityAttributeEntity;
import com.github.nramc.dev.journey.api.core.utils.EmailAddressObfuscator;
import lombok.experimental.UtilityClass;

@UtilityClass
public class UserSecurityAttributeConverter {

    public static UserSecurityAttribute toModel(final UserSecurityAttributeEntity entity) {
        return UserSecurityAttribute.builder()
                .type(entity.getType())
                .value(entity.getValue())
                .enabled(entity.isEnabled())
                .verified(entity.isVerified())
                .creationDate(entity.getCreationDate())
                .lastUpdateDate(entity.getLastUpdateDate())
                .build();
    }

    public static UserSecurityAttribute toResponse(final UserSecurityAttribute attribute) {
        return UserSecurityAttribute.builder()
                .type(attribute.type())
                .value(obfuscate(attribute))
                .enabled(attribute.enabled())
                .verified(attribute.verified())
                .creationDate(attribute.creationDate())
                .lastUpdateDate(attribute.lastUpdateDate())
                .build();
    }

    private static String obfuscate(UserSecurityAttribute attribute) {
        return switch (attribute.type()) {
            case EMAIL_ADDRESS -> EmailAddressObfuscator.obfuscate(attribute.value());
            case TOTP -> "***";
        };
    }

}
