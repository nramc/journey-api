package com.github.nramc.dev.journey.api.repository.user.attributes;

import com.github.nramc.dev.journey.api.repository.user.AuthUser;
import com.github.nramc.dev.journey.api.core.domain.user.UserSecurityAttribute;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

@RequiredArgsConstructor
public class UserSecurityAttributeService {
    private final UserSecurityAttributesRepository attributesRepository;

    public List<UserSecurityAttribute> getAllAvailableUserSecurityAttributes(AuthUser userDetails) {
        List<UserSecurityAttributeEntity> attributeEntities = attributesRepository.findAllByUserId(userDetails.getId().toHexString());
        return CollectionUtils.emptyIfNull(attributeEntities).stream().map(UserSecurityAttributeConverter::toModel).toList();
    }

}
