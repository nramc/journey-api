package com.github.nramc.dev.journey.api.repository.user.attributes;

import com.github.nramc.dev.journey.api.core.domain.user.UserSecurityAttribute;
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

}
