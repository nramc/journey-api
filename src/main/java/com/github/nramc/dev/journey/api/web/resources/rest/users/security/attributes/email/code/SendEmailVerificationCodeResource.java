package com.github.nramc.dev.journey.api.web.resources.rest.users.security.attributes.email.code;

import com.github.nramc.dev.journey.api.repository.user.AuthUser;
import com.github.nramc.dev.journey.api.web.resources.rest.doc.RestDocCommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

import static com.github.nramc.dev.journey.api.web.resources.Resources.SEND_EMAIL_CODE;
import static com.github.nramc.dev.journey.api.web.resources.rest.users.security.confirmationcode.ConfirmationUseCase.VERIFY_EMAIL_ADDRESS;

@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "My Account Security - Email Address Settings")
public class SendEmailVerificationCodeResource {
    private final UserDetailsManager userDetailsManager;
    private final EmailConfirmationCodeService emailConfirmationCodeService;

    @Operation(summary = "Send Email Confirmation Code to registered email address")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Email code has been send successfully")})
    @RestDocCommonResponse
    @PostMapping(value = SEND_EMAIL_CODE)
    public void sendEmailCode(Authentication authentication) {
        AuthUser authUser = Optional.of(authentication)
                .map(Authentication::getName)
                .map(userDetailsManager::loadUserByUsername)
                .map(AuthUser.class::cast)
                .orElseThrow(() -> new AccessDeniedException("User does not exists"));

        emailConfirmationCodeService.send(authUser, VERIFY_EMAIL_ADDRESS);

        log.info("Email Code has been sent successfully");
    }
}
