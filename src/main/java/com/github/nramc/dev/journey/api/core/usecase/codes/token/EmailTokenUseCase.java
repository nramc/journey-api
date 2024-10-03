package com.github.nramc.dev.journey.api.core.usecase.codes.token;

import com.github.nramc.dev.journey.api.core.domain.AppUser;
import com.github.nramc.dev.journey.api.core.domain.EmailToken;
import com.github.nramc.dev.journey.api.core.domain.user.ConfirmationCodeType;
import com.github.nramc.dev.journey.api.repository.user.code.ConfirmationCodeEntity;
import com.github.nramc.dev.journey.api.repository.user.code.ConfirmationCodeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Slf4j
public class EmailTokenUseCase {
    private final ConfirmationCodeRepository codeRepository;

    public EmailToken generateEmailToken(AppUser appUser) {
        EmailToken emailToken = EmailToken.valueOf(UUID.randomUUID().toString());
        saveEmailToken(emailToken, appUser);
        return emailToken;
    }

    public boolean verifyEmailToken(EmailToken emailToken, AppUser appUser) {
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
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .build();
        codeRepository.save(entity);
    }


}
