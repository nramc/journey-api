package com.github.nramc.dev.journey.api.core.usecase.codes.emailcode;

import com.github.nramc.dev.journey.api.core.domain.user.ConfirmationCodeType;
import com.github.nramc.dev.journey.api.core.usecase.codes.ConfirmationCode;
import com.github.nramc.dev.journey.api.core.usecase.codes.EmailCode;
import com.github.nramc.dev.journey.api.repository.user.AuthUser;
import com.github.nramc.dev.journey.api.repository.user.ConfirmationCodeEntity;
import com.github.nramc.dev.journey.api.repository.user.ConfirmationCodeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
public class EmailCodeValidator {
    static final int EMAIL_CODE_VALIDITY_MINUTES = 15;
    private final ConfirmationCodeRepository codeRepository;

    /**
     * @param confirmationCode code to be validated
     * @param authUser         email code associated user
     * @return true if code valid otherwise false
     */
    public boolean isValid(ConfirmationCode confirmationCode, AuthUser authUser) {
        EmailCode code = (EmailCode) confirmationCode;
        List<ConfirmationCodeEntity> codes = codeRepository.findAllByUsername(authUser.getUsername());

        ConfirmationCodeEntity confirmationCodeEntity = CollectionUtils.emptyIfNull(codes).stream()
                .filter(entity -> StringUtils.equals(entity.getCode(), code.code())).findFirst().orElse(null);

        if (CollectionUtils.isEmpty(codes) || confirmationCodeEntity == null) { // Email Code does not exists for user
            log.info("Email Code verification failed. Reason:[code not exists]");
            return false;
        }
        if (!confirmationCodeEntity.isActive()) { // Email code is already verified
            log.info("Email Code verification failed. Reason:[code not active]");
            return false;
        }
        if (!ConfirmationCodeType.EMAIL_CODE.equals(confirmationCodeEntity.getType())) {
            log.info("Email Code verification failed. Reason:[Confirmation Type not matched]");
            return false;
        }
        // check whether code is not expired
        if (Duration.between(confirmationCodeEntity.getCreatedAt(), LocalDateTime.now()).toMinutes() > EMAIL_CODE_VALIDITY_MINUTES) {
            log.info("Email Code verification failed. Reason:[Code expired]");
            return false;
        }

        return true;
    }

}
