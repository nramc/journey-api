package com.github.nramc.dev.journey.api.services.email;

import com.github.nramc.dev.journey.api.models.core.ConfirmationCodeType;
import com.github.nramc.dev.journey.api.repository.auth.AuthUser;
import com.github.nramc.dev.journey.api.repository.security.ConfirmationCodeEntity;
import com.github.nramc.dev.journey.api.repository.security.ConfirmationCodeRepository;
import com.github.nramc.dev.journey.api.services.MailService;
import com.github.nramc.dev.journey.api.services.confirmationcode.ConfirmationCode;
import com.github.nramc.dev.journey.api.services.confirmationcode.ConfirmationCodeService;
import com.github.nramc.dev.journey.api.services.confirmationcode.ConfirmationUseCase;
import com.github.nramc.dev.journey.api.web.exceptions.TechnicalException;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.map.HashedMap;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import static com.github.nramc.dev.journey.api.services.email.EmailHelper.getSubject;

@RequiredArgsConstructor
@Slf4j
public class EmailConfirmationCodeService implements ConfirmationCodeService {
    static final int CODE_LENGTH = 6;
    static final String EMAIL_CODE_TEMPLATE_HTML = "email-code-template.html";
    private static final SecureRandom RANDOM = new SecureRandom();
    private final MailService mailService;
    private final ConfirmationCodeRepository codeRepository;
    private final EmailCodeValidator emailCodeValidator;

    /**
     * Generate Email code securely
     * Send the generated email code to user's registered email address
     * Persist email code, and it's associated email address and username, sent time in dedicated table
     * Email Code validity is configurable e.g. 15 minutes
     *
     * @param authUser User to whom email code to be sent
     */
    @Override
    public void send(AuthUser authUser, ConfirmationUseCase useCase) {

        EmailCode emailCode = generateEmailCode();

        sendEmailCode(useCase, emailCode, authUser);

        saveEmailCode(useCase, emailCode, authUser);

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
    @Override
    public boolean verify(ConfirmationCode confirmationCode, AuthUser authUser, ConfirmationUseCase useCase) {
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

    private void sendEmailCode(ConfirmationUseCase useCase, EmailCode emailCode, AuthUser authUser) {
        try {
            Map<String, Object> parameters = new HashedMap<>();
            parameters.put("name", authUser.getName());
            parameters.put("ottPin", emailCode.code());

            mailService.sendEmailUsingTemplate(EMAIL_CODE_TEMPLATE_HTML, authUser.getEmailAddress(), getSubject(useCase), parameters);
        } catch (RuntimeException | MessagingException ex) {
            throw new TechnicalException("Unable to send Email Code", ex);
        }
    }

    private void saveEmailCode(ConfirmationUseCase useCase, EmailCode code, AuthUser authUser) {
        ConfirmationCodeEntity entity = ConfirmationCodeEntity.builder()
                .id(UUID.randomUUID().toString())
                .type(ConfirmationCodeType.EMAIL_CODE)
                .code(code.code())
                .username(authUser.getUsername())
                .receiver(authUser.getEmailAddress())
                .isActive(true)
                .createdAt(LocalDateTime.now().minusMinutes(2))
                .useCase(useCase)
                .build();
        codeRepository.save(entity);
    }

    private void invalidateAllCodes(AuthUser authUser) {
        List<ConfirmationCodeEntity> entities = codeRepository.findAllByUsername(authUser.getUsername());
        codeRepository.deleteAll(entities);
    }

}
