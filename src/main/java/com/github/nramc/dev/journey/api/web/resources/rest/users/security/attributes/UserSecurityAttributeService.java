package com.github.nramc.dev.journey.api.web.resources.rest.users.security.attributes;

import com.github.nramc.dev.journey.api.repository.auth.AuthUser;
import com.github.nramc.dev.journey.api.repository.auth.UserSecurityAttributeEntity;
import com.github.nramc.dev.journey.api.repository.auth.UserSecurityAttributesRepository;
import com.github.nramc.dev.journey.api.web.dto.user.security.UserSecurityAttribute;
import com.github.nramc.dev.journey.api.web.dto.user.security.UserSecurityAttributeConverter;
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
