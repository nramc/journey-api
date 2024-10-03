package com.github.nramc.dev.journey.api.core.services.token;

import com.github.nramc.dev.journey.api.core.domain.AppUser;
import com.github.nramc.dev.journey.api.core.domain.EmailToken;
import com.github.nramc.dev.journey.api.core.security.attributes.recovery.code.ConfirmationCodeType;
import com.github.nramc.dev.journey.api.repository.security.ConfirmationCodeEntity;
import com.github.nramc.dev.journey.api.repository.security.ConfirmationCodeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static com.github.nramc.dev.journey.api.web.resources.rest.users.security.confirmationcode.ConfirmationUseCase.UNKNOWN;

@RequiredArgsConstructor
@Slf4j
public class EmailTokenService {
    private final ConfirmationCodeRepository codeRepository;

    public EmailToken generateEmailToken(AppUser appUser) {
        EmailToken emailToken = EmailToken.valueOf(UUID.randomUUID().toString());
        saveEmailToken(emailToken, appUser);
        return emailToken;
    }

    public boolean isTokenExistsAndValid(EmailToken emailToken, AppUser appUser) {
        List<ConfirmationCodeEntity> codeEntities = codeRepository.findAllByUsername(appUser.username());
        return CollectionUtils.emptyIfNull(codeEntities).stream()
                .filter(ConfirmationCodeEntity::isActive)
                .filter(entity -> ConfirmationCodeType.EMAIL_TOKEN == entity.getType())
                .anyMatch(entity -> StringUtils.equals(entity.getCode(), emailToken.token()));
    }

    private void saveEmailToken(EmailToken emailToken, AppUser appUser) {
        ConfirmationCodeEntity entity = ConfirmationCodeEntity.builder()
                .id(UUID.randomUUID().toString())
                .type(ConfirmationCodeType.EMAIL_TOKEN)
                .code(emailToken.token())
                .username(appUser.username())
                .receiver(appUser.username())
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .useCase(UNKNOWN)
                .build();
        codeRepository.save(entity);
    }


}
