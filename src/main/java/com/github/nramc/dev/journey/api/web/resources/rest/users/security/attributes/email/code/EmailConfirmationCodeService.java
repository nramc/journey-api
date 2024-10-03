package com.github.nramc.dev.journey.api.web.resources.rest.users.security.attributes.email.code;

import com.github.nramc.dev.journey.api.core.domain.user.ConfirmationCodeType;
import com.github.nramc.dev.journey.api.core.domain.user.UserSecurityAttribute;
import com.github.nramc.dev.journey.api.core.exceptions.BusinessException;
import com.github.nramc.dev.journey.api.core.exceptions.TechnicalException;
import com.github.nramc.dev.journey.api.core.services.mail.MailService;
import com.github.nramc.dev.journey.api.repository.user.AuthUser;
import com.github.nramc.dev.journey.api.repository.user.ConfirmationCodeEntity;
import com.github.nramc.dev.journey.api.repository.user.ConfirmationCodeRepository;
import com.github.nramc.dev.journey.api.web.resources.rest.users.security.attributes.email.UserSecurityEmailAddressAttributeService;
import com.github.nramc.dev.journey.api.web.resources.rest.users.security.confirmationcode.ConfirmationCode;
import com.github.nramc.dev.journey.api.web.resources.rest.users.security.confirmationcode.EmailCode;
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

@RequiredArgsConstructor
@Slf4j
public class EmailConfirmationCodeService {
    static final int CODE_LENGTH = 6;
    public static final String EMAIL_CODE_TEMPLATE_HTML = "email-code-template.html";
    private static final SecureRandom RANDOM = new SecureRandom();
    private final MailService mailService;
    private final ConfirmationCodeRepository codeRepository;
    private final EmailCodeValidator emailCodeValidator;
    private final UserSecurityEmailAddressAttributeService emailAddressAttributeService;

    /**
     * Generate Email code securely
     * Send the generated email code to user's registered email address
     * Persist email code, and it's associated email address and username, sent time in dedicated table
     * Email Code validity is configurable e.g. 15 minutes
     *
     * @param authUser User to whom email code to be sent
     */
    public void send(AuthUser authUser) {

        UserSecurityAttribute emailAttribute = getUserEmailSecurityAttribute(authUser);

        EmailCode emailCode = generateEmailCode();

        sendEmailCode(emailCode, authUser, emailAttribute);

        saveEmailCode(emailCode, authUser, emailAttribute);

        log.info("Email Code has been sent to registered email address");
    }

    private UserSecurityAttribute getUserEmailSecurityAttribute(AuthUser authUser) {
        return emailAddressAttributeService.provideEmailAttributeIfExists(authUser)
                .orElseThrow(() -> new BusinessException("email.not.exists", "Email not registered"));
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
            emailAddressAttributeService.setVerifiedStatus(true, authUser);
            invalidateAllCodes(authUser);
            log.info("Email Code verified successfully and all codes invalidated");
        }
        return isEmailCodeValid;
    }


    EmailCode generateEmailCode() {
        int code = Random.from(RANDOM).nextInt(100000, 999999);
        return EmailCode.valueOf(code);
    }

    private void sendEmailCode(EmailCode emailCode, AuthUser authUser, UserSecurityAttribute emailAttribute) {
        try {
            Map<String, Object> parameters = new HashedMap<>();
            parameters.put("name", authUser.getName());
            parameters.put("ottPin", emailCode.code());

            mailService.sendEmailUsingTemplate(
                    EMAIL_CODE_TEMPLATE_HTML, emailAttribute.value(), "Journey: Confirmation Required", parameters);
        } catch (RuntimeException | MessagingException ex) {
            throw new TechnicalException("Unable to send Email Code", ex);
        }
    }

    private void saveEmailCode(EmailCode code, AuthUser authUser, UserSecurityAttribute emailAttribute) {
        ConfirmationCodeEntity entity = ConfirmationCodeEntity.builder()
                .id(UUID.randomUUID().toString())
                .type(ConfirmationCodeType.EMAIL_CODE)
                .code(code.code())
                .username(authUser.getUsername())
                .receiver(emailAttribute.value())
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
