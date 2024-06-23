package com.github.nramc.dev.journey.api.web.resources.rest.account.security;

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
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;
import java.util.Optional;

import static com.github.nramc.dev.journey.api.web.resources.Resources.SEND_EMAIL_CODE;

@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Account Email Address Verification Resource", description = "Verify account email address with email code verification")

public class EmailAddressVerificationResource {
    private final UserDetailsService userDetailsService;
    private final EmailConfirmationCodeService emailConfirmationCodeService;

    @Operation(summary = "Send Email Code to registered email address")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Email code has been send successfully")})
    @RestDocCommonResponse
    @PostMapping(value = SEND_EMAIL_CODE)
    public void sendEmailCode(Authentication authentication) {
        AuthUser authUser = Optional.of(authentication)
                .map(Authentication::getName)
                .map(userDetailsService::loadUserByUsername)
                .map(AuthUser.class::cast)
                .orElseThrow(() -> new AccessDeniedException("User does not exists"));

        validate(authUser);
        emailConfirmationCodeService.send(authUser, "Journey: Email Verification Request");

        log.info("Email Code has been sent successfully");
    }

    private void validate(AuthUser authUser) {
        Objects.requireNonNull(authUser.getEmailAddress(), "Email address not exists for the user");
    }
}
