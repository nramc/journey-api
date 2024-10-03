package com.github.nramc.dev.journey.api.repository.user.attributes;

import com.github.nramc.dev.journey.api.core.domain.user.UserSecurityAttribute;
import com.github.nramc.dev.journey.api.core.domain.user.UserSecurityAttributeType;
import com.github.nramc.dev.journey.api.core.usecase.codes.totp.TotpSecret;
import com.github.nramc.dev.journey.api.repository.user.AuthUser;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class UserSecurityAttributeService {
    private final UserSecurityAttributesRepository attributesRepository;

    public List<UserSecurityAttribute> getAllAvailableUserSecurityAttributes(AuthUser userDetails) {
        List<UserSecurityAttributeEntity> attributeEntities = attributesRepository.findAllByUserId(userDetails.getId().toHexString());
        return CollectionUtils.emptyIfNull(attributeEntities).stream().map(UserSecurityAttributeConverter::toModel).toList();
    }

    public Optional<UserSecurityAttribute> getAttributeByType(AuthUser userDetails, UserSecurityAttributeType type) {
        List<UserSecurityAttributeEntity> attributes = attributesRepository.findAllByUserIdAndType(userDetails.getId().toHexString(), type);
        return Optional.ofNullable(attributes)
                .filter(CollectionUtils::isNotEmpty)
                .map(List::getFirst)
                .map(UserSecurityAttributeConverter::toModel);
    }

    public void saveTOTPSecret(AuthUser authUser, TotpSecret secret) {
        UserSecurityAttributeEntity totpAttribute = UserSecurityAttributeEntity.builder()
                .type(UserSecurityAttributeType.TOTP)
                .value(secret.secret())
                .userId(authUser.getId().toHexString())
                .username(authUser.getUsername())
                .creationDate(LocalDate.now())
                .lastUpdateDate(LocalDate.now())
                .enabled(true)
                .verified(true)
                .build();
        attributesRepository.save(totpAttribute);
    }

    public void deleteAttributeByType(AuthUser userDetails, UserSecurityAttributeType type) {
        getAttributeByType(userDetails, type).ifPresent(attribute ->
                attributesRepository.deleteAllByUserIdAndType(userDetails.getId().toHexString(), UserSecurityAttributeType.TOTP));
    }

}
