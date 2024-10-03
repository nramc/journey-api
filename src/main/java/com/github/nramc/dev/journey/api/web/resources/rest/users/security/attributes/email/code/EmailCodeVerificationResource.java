package com.github.nramc.dev.journey.api.web.resources.rest.users.security.attributes.email.code;

import com.github.nramc.dev.journey.api.repository.user.AuthUser;
import com.github.nramc.dev.journey.api.web.resources.rest.doc.RestDocCommonResponse;
import com.github.nramc.dev.journey.api.web.resources.rest.users.security.confirmationcode.EmailCode;
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

import java.util.Optional;

import static com.github.nramc.dev.journey.api.web.resources.Resources.VERIFY_EMAIL_CODE;

@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "My Account Security - Email Address Settings")
public class EmailCodeVerificationResource {
    private final UserDetailsManager userDetailsManager;
    private final EmailConfirmationCodeService emailConfirmationCodeService;

    @Operation(summary = "Verify Email Confirmation code which was sent to registered email address")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Email code has been send successfully")})
    @RestDocCommonResponse
    @PostMapping(value = VERIFY_EMAIL_CODE)
    public ResponseEntity<Void> verifyEmailCode(Authentication authentication,
                                                @RequestBody @Valid EmailCodeVerificationRequest request) {
        AuthUser authUser = Optional.of(authentication)
                .map(Authentication::getName)
                .map(userDetailsManager::loadUserByUsername)
                .map(AuthUser.class::cast)
                .orElseThrow(() -> new AccessDeniedException("User does not exists"));

        boolean isValid = emailConfirmationCodeService.verify(
                EmailCode.valueOf(Integer.parseInt(request.code())),
                authUser);

        if (isValid) {
            log.info("Email Code has been verified successfully");
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().build();

    }


}
