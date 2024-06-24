package com.github.nramc.dev.journey.api.web.resources.rest.account.security;

import com.github.nramc.dev.journey.api.repository.auth.AuthUser;
import com.github.nramc.dev.journey.api.services.email.EmailCode;
import com.github.nramc.dev.journey.api.services.email.EmailConfirmationCodeService;
import com.github.nramc.dev.journey.api.web.resources.rest.doc.RestDocCommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;
import java.util.Optional;

import static com.github.nramc.dev.journey.api.services.confirmationcode.ConfirmationUseCase.VERIFY_EMAIL_ADDRESS;
import static com.github.nramc.dev.journey.api.web.resources.Resources.SEND_EMAIL_CODE;
import static com.github.nramc.dev.journey.api.web.resources.Resources.VERIFY_EMAIL_CODE;

@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Account Email Address Verification Resource", description = "Verify account email address with email code verification")

public class EmailAddressVerificationResource {
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

    @Operation(summary = "Send Email Confirmation Code to registered email address")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Email code has been send successfully")})
    @RestDocCommonResponse
    @PostMapping(value = VERIFY_EMAIL_CODE)
    public ResponseEntity<Void> verifyEmailCode(Authentication authentication,
                                                @RequestBody @Valid EmailVerificationRequest request) {
        AuthUser authUser = Optional.of(authentication)
                .map(Authentication::getName)
                .map(userDetailsService::loadUserByUsername)
                .map(AuthUser.class::cast)
                .orElseThrow(() -> new AccessDeniedException("User does not exists"));

        Objects.requireNonNull(authUser.getEmailAddress(), "Email address not exists for the user");

        boolean isValid = emailConfirmationCodeService.verify(
                EmailCode.valueOf(Integer.parseInt(request.code())),
                authUser, VERIFY_EMAIL_ADDRESS);

        if (isValid) {
            AuthUser updatedUserDetails = authUser.toBuilder().isEmailAddressVerified(true).build();
            userDetailsService.updateUser(updatedUserDetails);
            log.info("Email Code has been sent successfully");
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().build();

    }


}
