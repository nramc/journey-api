package com.github.nramc.dev.journey.api.account.codes.emailcode;

import com.github.nramc.dev.journey.api.account.EmailCodeRequestedEvent;
import com.github.nramc.dev.journey.api.account.repository.AuthUser;
import com.github.nramc.dev.journey.api.account.repository.code.ConfirmationCodeEntity;
import com.github.nramc.dev.journey.api.account.repository.code.ConfirmationCodeRepository;
import com.github.nramc.dev.journey.api.shared.domain.user.security.ConfirmationCode;
import com.github.nramc.dev.journey.api.shared.domain.user.security.ConfirmationCodeType;
import com.github.nramc.dev.journey.api.shared.domain.user.security.EmailCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.map.HashedMap;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

@RequiredArgsConstructor
@Slf4j
@Transactional
public class EmailCodeUseCase {
    static final int CODE_LENGTH = 6;
    private static final SecureRandom RANDOM = new SecureRandom();
    private final ConfirmationCodeRepository codeRepository;
    private final EmailCodeValidator emailCodeValidator;
    private final ApplicationEventPublisher applicationEvents;

    /**
     * Generate Email code securely
     * Send the generated email code to user's registered email address
     * Persist email code, and it's associated email address and username, sent time in dedicated table
     * Email Code validity is configurable e.g. 15 minutes
     *
     * @param authUser User to whom email code to be sent
     */
    public void send(AuthUser authUser) {

        EmailCode emailCode = generateEmailCode();

        sendEmailCodeEvent(emailCode, authUser);

        saveEmailCode(emailCode, authUser);

        log.info("Email Code has been sent to registered email address");
    }

    /**
     * Verify given Email Code for authenticated user.
     * In order to consider code is valid, below conditions should be satisfied
     * 1. Email Code should exist
     * 2. Email Address should match
     * 3. Email Code should not have expired e.g. duration should be less than 15 minutes
     * 4. Can be validated only once
     *
     * @return true if email code matched otherwise false
     */
    public boolean verify(ConfirmationCode confirmationCode, AuthUser authUser) {
        boolean isEmailCodeValid = emailCodeValidator.isValid(confirmationCode, authUser);

        if (isEmailCodeValid) {
            invalidateAllCodes(authUser);
            log.info("Email Code verified successfully and all codes invalidated");
        }
        return isEmailCodeValid;
    }


    EmailCode generateEmailCode() {
        int code = Random.from(RANDOM).nextInt(100000, 999999);
        return EmailCode.valueOf(code);
    }

    private void sendEmailCodeEvent(EmailCode emailCode, AuthUser authUser) {
        Map<String, Object> parameters = new HashedMap<>();
        parameters.put("name", authUser.getName());
        parameters.put("ottPin", emailCode.code());

        applicationEvents.publishEvent(new EmailCodeRequestedEvent(authUser.getUsername(), parameters));
    }

    private void saveEmailCode(EmailCode code, AuthUser authUser) {
        ConfirmationCodeEntity entity = ConfirmationCodeEntity.builder()
                .id(UUID.randomUUID().toString())
                .type(ConfirmationCodeType.EMAIL_CODE)
                .code(code.code())
                .username(authUser.getUsername())
                .isActive(true)
                .createdAt(LocalDateTime.now().minusMinutes(2))
                .build();
        codeRepository.save(entity);
    }

    private void invalidateAllCodes(AuthUser authUser) {
        List<ConfirmationCodeEntity> entities = codeRepository.findAllByUsername(authUser.getUsername());
        codeRepository.deleteAll(entities);
    }

}
