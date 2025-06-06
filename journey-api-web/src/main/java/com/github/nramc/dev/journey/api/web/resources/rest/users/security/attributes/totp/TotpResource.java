package com.github.nramc.dev.journey.api.web.resources.rest.users.security.attributes.totp;

import com.github.nramc.dev.journey.api.core.domain.user.settings.security.TotpSecret;
import com.github.nramc.dev.journey.api.core.usecase.codes.TotpCode;
import com.github.nramc.dev.journey.api.core.usecase.codes.totp.QRImageDetails;
import com.github.nramc.dev.journey.api.core.usecase.codes.totp.TotpUseCase;
import com.github.nramc.dev.journey.api.repository.user.AuthUser;
import com.github.nramc.dev.journey.api.web.resources.rest.doc.RestDocCommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static com.github.nramc.dev.journey.api.web.resources.Resources.MY_SECURITY_ATTRIBUTE_TOTP;
import static com.github.nramc.dev.journey.api.web.resources.Resources.MY_SECURITY_ATTRIBUTE_TOTP_STATUS;
import static com.github.nramc.dev.journey.api.web.resources.Resources.MY_SECURITY_ATTRIBUTE_TOTP_VERIFY;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@Slf4j
@RequiredArgsConstructor
@Tag(name = "My Account Security - Totp Settings")
@Validated
public class TotpResource {
    private final UserDetailsService userDetailsService;
    private final TotpUseCase totpUseCase;

    // 1. new key
    @Operation(summary = "Generate QR Code with new TOTP secret key")
    @RestDocCommonResponse
    @ApiResponse(responseCode = "200", description = "new totp secret key")
    @GetMapping(value = MY_SECURITY_ATTRIBUTE_TOTP, produces = APPLICATION_JSON_VALUE)
    public QRImageDetails generateSecret(Authentication authentication) {
        AuthUser authUser = (AuthUser) userDetailsService.loadUserByUsername(authentication.getName());
        return totpUseCase.newQRCodeData(authUser);
    }

    // 2. activate totp
    @Operation(summary = "Verify Code for the new secret key and activate TOTP 2FA for the user")
    @RestDocCommonResponse
    @ApiResponse(responseCode = "200", description = "2FA TOTP activated")
    @PostMapping(value = MY_SECURITY_ATTRIBUTE_TOTP, consumes = APPLICATION_JSON_VALUE)
    public void activate(
            @RequestBody @Valid TotpActivationRequest activationRequest,
            Authentication authentication) {
        AuthUser authUser = (AuthUser) userDetailsService.loadUserByUsername(authentication.getName());
        totpUseCase.activateTotp(authUser,
                TotpCode.valueOf(activationRequest.code()),
                TotpSecret.valueOf(activationRequest.secretKey())
        );
    }

    @Operation(summary = "Status of TOTP 2FA for the user")
    @RestDocCommonResponse
    @ApiResponse(responseCode = "200", description = "2FA TOTP Status")
    @GetMapping(value = MY_SECURITY_ATTRIBUTE_TOTP_STATUS, produces = APPLICATION_JSON_VALUE)
    public TotpStatus status(Authentication authentication) {
        AuthUser authUser = (AuthUser) userDetailsService.loadUserByUsername(authentication.getName());
        return new TotpStatus(totpUseCase.getTotpAttributeIfExists(authUser).isPresent());
    }


    // 3. deactivate totp
    @Operation(summary = "Deactivate TOTP 2FA for the user")
    @RestDocCommonResponse
    @ApiResponse(responseCode = "200", description = "2FA TOTP activated")
    @DeleteMapping(value = MY_SECURITY_ATTRIBUTE_TOTP)
    public void deactivate(Authentication authentication) {
        AuthUser authUser = (AuthUser) userDetailsService.loadUserByUsername(authentication.getName());
        totpUseCase.deactivateTotp(authUser);
    }

    // 4. verify totp
    @Operation(summary = "Verify Code for the user")
    @RestDocCommonResponse
    @ApiResponse(responseCode = "200", description = "2FA TOTP activated")
    @PostMapping(value = MY_SECURITY_ATTRIBUTE_TOTP_VERIFY, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public VerifyTotpCodeResponse verify(
            @RequestBody @Valid VerifyTotpCodeRequest verifyRequest,
            Authentication authentication) {
        AuthUser authUser = (AuthUser) userDetailsService.loadUserByUsername(authentication.getName());
        return new VerifyTotpCodeResponse(totpUseCase.verify(authUser, TotpCode.valueOf(verifyRequest.code())));
    }

}
