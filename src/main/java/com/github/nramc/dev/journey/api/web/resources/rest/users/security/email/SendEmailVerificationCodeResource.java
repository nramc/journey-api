package com.github.nramc.dev.journey.api.web.resources.rest.users.security.email;

import com.github.nramc.dev.journey.api.repository.auth.AuthUser;
import com.github.nramc.dev.journey.api.services.email.EmailConfirmationCodeService;
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

import java.util.Objects;
import java.util.Optional;

import static com.github.nramc.dev.journey.api.services.confirmationcode.ConfirmationUseCase.VERIFY_EMAIL_ADDRESS;
import static com.github.nramc.dev.journey.api.web.resources.Resources.SEND_EMAIL_CODE;

@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Sending Email Address Verification Code Resource", description = "Verify security email address with email code verification")
public class SendEmailVerificationCodeResource {
    private final UserDetailsManager userDetailsService;
    private final EmailConfirmationCodeService emailConfirmationCodeService;

    @Operation(summary = "Send Email Confirmation Code to registered email address")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Email code has been send successfully")})
    @RestDocCommonResponse
    @PostMapping(value = SEND_EMAIL_CODE)
    public void sendEmailCode(Authentication authentication) {
        AuthUser authUser = Optional.of(authentication)
                .map(Authentication::getName)
                .map(userDetailsService::loadUserByUsername)
                .map(AuthUser.class::cast)
                .orElseThrow(() -> new AccessDeniedException("User does not exists"));

        Objects.requireNonNull(authUser.getEmailAddress(), "Email address not exists for the user");
        emailConfirmationCodeService.send(authUser, VERIFY_EMAIL_ADDRESS);

        log.info("Email Code has been sent successfully");
    }
}
