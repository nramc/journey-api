package com.github.nramc.dev.journey.api.services.email;

import com.github.nramc.dev.journey.api.models.core.ConfirmationCodeType;
import com.github.nramc.dev.journey.api.repository.auth.AuthUser;
import com.github.nramc.dev.journey.api.repository.security.ConfirmationCodeEntity;
import com.github.nramc.dev.journey.api.repository.security.ConfirmationCodeRepository;
import com.github.nramc.dev.journey.api.services.confirmationcode.ConfirmationCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.time.LocalDateTime;

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
        EmailCode emailCode = (EmailCode) confirmationCode;
        ConfirmationCodeEntity confirmationCodeEntity = codeRepository.findByUsernameAndCode(authUser.getUsername(), emailCode.code());

        if (confirmationCodeEntity == null) { // Email Code does not exists for user
            log.info("Email Code verification failed. Reason:[code not exists]");
            return false;
        }
        if (!confirmationCodeEntity.isActive()) { // Email code is already verified
            log.info("Email Code verification failed. Reason:[code not active]");
            return false;
        }
        if (!confirmationCodeEntity.getReceiver().equals(authUser.getEmailAddress())) { // Email Address not matched
            log.info("Email Code verification failed. Reason:[Email address not matched]");
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
