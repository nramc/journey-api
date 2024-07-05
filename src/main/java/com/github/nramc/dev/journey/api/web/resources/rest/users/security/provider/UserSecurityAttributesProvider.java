package com.github.nramc.dev.journey.api.web.resources.rest.users.security.provider;

import com.github.nramc.dev.journey.api.models.core.SecurityAttributeType;
import com.github.nramc.dev.journey.api.repository.auth.AuthUser;
import com.github.nramc.dev.journey.api.repository.auth.UserSecurityAttributesEntity;
import com.github.nramc.dev.journey.api.repository.auth.UserSecurityAttributesRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class UserSecurityAttributesProvider {
    private final UserSecurityAttributesRepository userSecurityAttributesRepository;

    public List<UserSecurityAttributesEntity> provide(AuthUser authUser) {
        return userSecurityAttributesRepository.findAllByUserId(authUser.getId().toHexString());
    }

    public Optional<UserSecurityAttributesEntity> provideEmailAttributeIfExists(AuthUser authUser) {
        List<UserSecurityAttributesEntity> attributesEntities = userSecurityAttributesRepository
                .findAllByUserIdAndType(authUser.getId().toHexString(), SecurityAttributeType.EMAIL_ADDRESS);

        return Optional.of(attributesEntities).filter(CollectionUtils::isNotEmpty).map(List::getFirst);
    }

}
