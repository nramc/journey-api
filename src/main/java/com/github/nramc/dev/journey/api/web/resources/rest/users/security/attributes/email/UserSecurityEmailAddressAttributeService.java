package com.github.nramc.dev.journey.api.web.resources.rest.users.security.attributes.email;

import com.github.nramc.dev.journey.api.core.domain.EmailAddress;
import com.github.nramc.dev.journey.api.core.domain.user.UserSecurityAttributeType;
import com.github.nramc.dev.journey.api.repository.user.AuthUser;
import com.github.nramc.dev.journey.api.repository.user.UserSecurityAttributeEntity;
import com.github.nramc.dev.journey.api.repository.user.UserSecurityAttributesRepository;
import com.github.nramc.dev.journey.api.core.domain.user.UserSecurityAttribute;
import com.github.nramc.dev.journey.api.web.dto.user.security.UserSecurityAttributeConverter;
import com.github.nramc.dev.journey.api.core.exceptions.BusinessException;
import com.github.nramc.dev.journey.api.web.resources.rest.users.security.utils.SecurityAttributesUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class UserSecurityEmailAddressAttributeService {
    private final UserSecurityAttributesRepository userSecurityAttributesRepository;

    public List<UserSecurityAttributeEntity> provide(AuthUser authUser) {
        return userSecurityAttributesRepository.findAllByUserId(authUser.getId().toHexString());
    }

    public Optional<UserSecurityAttribute> provideEmailAttributeIfExists(AuthUser authUser) {
        List<UserSecurityAttributeEntity> attributesEntities = userSecurityAttributesRepository
                .findAllByUserIdAndType(authUser.getId().toHexString(), UserSecurityAttributeType.EMAIL_ADDRESS);

        return Optional.of(attributesEntities)
                .filter(CollectionUtils::isNotEmpty)
                .map(List::getFirst)
                .map(UserSecurityAttributeConverter::toModel);
    }

    public UserSecurityAttribute saveSecurityEmailAddress(AuthUser authUser, EmailAddress emailAddress) {
        List<UserSecurityAttributeEntity> attributesEntities = userSecurityAttributesRepository
                .findAllByUserIdAndType(authUser.getId().toHexString(), UserSecurityAttributeType.EMAIL_ADDRESS);

        UserSecurityAttributeEntity emailAttribute = Optional.of(attributesEntities)
                .filter(CollectionUtils::isNotEmpty)
                .map(List::getFirst)
                .orElse(SecurityAttributesUtils.newEmailAttribute(authUser));

        UserSecurityAttributeEntity updatedAttribute = emailAttribute.toBuilder()
                .value(emailAddress.value())
                .enabled(true)
                .verified(false)
                .lastUpdateDate(LocalDate.now())
                .build();
        UserSecurityAttributeEntity savedEntity = userSecurityAttributesRepository.save(updatedAttribute);
        return UserSecurityAttributeConverter.toModel(savedEntity);
    }

    public void setVerifiedStatus(boolean status, AuthUser authUser) {
        List<UserSecurityAttributeEntity> attributesEntities = userSecurityAttributesRepository
                .findAllByUserIdAndType(authUser.getId().toHexString(), UserSecurityAttributeType.EMAIL_ADDRESS);

        UserSecurityAttributeEntity emailAttribute = Optional.of(attributesEntities)
                .filter(CollectionUtils::isNotEmpty)
                .map(List::getFirst)
                .orElseThrow(() -> new BusinessException("Email Security Attribute not exists", "email.not.exists"));

        UserSecurityAttributeEntity updatedAttribute = emailAttribute.toBuilder()
                .enabled(status)
                .verified(status)
                .lastUpdateDate(LocalDate.now())
                .build();
        userSecurityAttributesRepository.save(updatedAttribute);
    }

}
