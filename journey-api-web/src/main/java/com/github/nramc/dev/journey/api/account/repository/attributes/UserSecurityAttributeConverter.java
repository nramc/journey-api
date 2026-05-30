package com.github.nramc.dev.journey.api.account.repository.attributes;

import com.github.nramc.dev.journey.api.shared.domain.user.UserSecurityAttribute;

public final class UserSecurityAttributeConverter {

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

    private UserSecurityAttributeConverter() {
        throw new IllegalStateException("Utility class");
    }

}
