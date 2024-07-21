package com.github.nramc.dev.journey.api.web.resources.rest.auth.mfa;

import com.github.nramc.dev.journey.api.repository.auth.AuthUser;
import com.github.nramc.dev.journey.api.web.resources.rest.auth.dto.LoginResponse;
import com.github.nramc.dev.journey.api.web.resources.rest.auth.dto.MultiFactorAuthenticationRequest;
import com.github.nramc.dev.journey.api.web.resources.rest.auth.jwt.JwtGenerator;
import com.github.nramc.dev.journey.api.web.resources.rest.doc.RestDocCommonResponse;
import com.github.nramc.dev.journey.api.web.resources.rest.users.security.confirmationcode.ConfirmationCode;
import com.github.nramc.dev.journey.api.web.resources.rest.users.security.confirmationcode.ConfirmationCodeVerifier;
import com.github.nramc.dev.journey.api.web.resources.rest.users.security.confirmationcode.EmailCode;
import com.github.nramc.dev.journey.api.web.resources.rest.users.security.confirmationcode.TotpCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

import static com.github.nramc.dev.journey.api.web.resources.Resources.LOGIN_MFA;

@RestController
@RequiredArgsConstructor
@Tag(name = "Login", description = "Multi factor authentication")
public class MultiFactorAuthenticationResource {
    private final UserDetailsService userDetailsService;
    private final ConfirmationCodeVerifier confirmationCodeVerifier;
    private final JwtGenerator jwtGenerator;

    @Operation(summary = "Multi factor authentication and retrieve JWT token")
    @RestDocCommonResponse
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Authentication successful and return JWT",
            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = LoginResponse.class))})})
    @PostMapping(LOGIN_MFA)
    public LoginResponse mfa(@AuthenticationPrincipal AuthUser userDetails,
                             @Valid @RequestBody MultiFactorAuthenticationRequest request) {
        AuthUser authenticatedUser = (AuthUser) userDetailsService.loadUserByUsername(userDetails.getUsername());
        if (verifyConfirmationCode(request, authenticatedUser)) {
            return jwtResponse(authenticatedUser);
        } else {
            throw new AccessDeniedException("Confirmation code verification failed");
        }
    }

    private boolean verifyConfirmationCode(MultiFactorAuthenticationRequest request, AuthUser authenticatedUser) {
        ConfirmationCode confirmationCode = switch (request.type()) {
            case EMAIL_ADDRESS -> EmailCode.valueOf(Integer.parseInt(request.value()));
            case TOTP -> TotpCode.valueOf(request.value());
        };
        return confirmationCodeVerifier.verify(confirmationCode, authenticatedUser);
    }

    private LoginResponse jwtResponse(AuthUser userDetails) {
        Jwt jwt = jwtGenerator.generate(userDetails);

        return LoginResponse.builder()
                .additionalFactorRequired(false)
                .token(jwt.getTokenValue())
                .expiredAt(jwt.getExpiresAt())
                .name(userDetails.getName())
                .authorities(Set.of(jwt.getClaimAsString("scope").split(" ")))
                .build();
    }
}
