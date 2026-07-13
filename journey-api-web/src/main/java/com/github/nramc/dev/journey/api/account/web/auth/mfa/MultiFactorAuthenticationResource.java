package com.github.nramc.dev.journey.api.account.web.auth.mfa;

import com.github.nramc.dev.journey.api.account.codes.ConfirmationCodeUseCase;
import com.github.nramc.dev.journey.api.account.jwt.JwtGenerator;
import com.github.nramc.dev.journey.api.account.repository.AuthUser;
import com.github.nramc.dev.journey.api.account.web.auth.dto.LoginResponse;
import com.github.nramc.dev.journey.api.account.web.auth.dto.MultiFactorAuthenticationRequest;
import com.github.nramc.dev.journey.api.shared.domain.user.security.ConfirmationCode;
import com.github.nramc.dev.journey.api.shared.domain.user.security.EmailCode;
import com.github.nramc.dev.journey.api.shared.domain.user.security.TotpCode;
import com.github.nramc.dev.journey.api.shared.web.doc.RestDocCommonResponse;
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

import java.util.Optional;
import java.util.Set;

import static com.github.nramc.dev.journey.api.shared.web.Resources.LOGIN_MFA;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequiredArgsConstructor
@Tag(name = "Multi Factor Login", description = "Complete multi-factor authentication")
public class MultiFactorAuthenticationResource {
    private final UserDetailsService userDetailsService;
    private final ConfirmationCodeUseCase confirmationCodeUseCase;
    private final JwtGenerator jwtGenerator;

    @Operation(summary = "Verify multi-factor code", description = "Verifies an email or TOTP code and returns a JWT.")
    @RestDocCommonResponse
    @ApiResponses(@ApiResponse(responseCode = "200", description = "Authentication successful and return JWT", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = LoginResponse.class))}))
    @PostMapping(value = LOGIN_MFA, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
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
        return confirmationCodeUseCase.verify(confirmationCode, authenticatedUser);
    }

    private LoginResponse jwtResponse(AuthUser userDetails) {
        Jwt jwt = jwtGenerator.generate(userDetails);

        return LoginResponse.builder()
                .additionalFactorRequired(false)
                .token(jwt.getTokenValue())
                .expiredAt(jwt.getExpiresAt())
                .name(userDetails.getName())
                .authorities(Optional.ofNullable(jwt.getClaimAsString("scope")).map(s -> Set.of(s.split(" "))).orElse(Set.of()))
                .build();
    }
}
