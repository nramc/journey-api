package com.github.nramc.dev.journey.api.web.resources.rest.users.security.totp;

import com.github.nramc.dev.journey.api.repository.auth.AuthUser;
import com.github.nramc.dev.journey.api.security.totp.model.TotpCode;
import com.github.nramc.dev.journey.api.security.totp.model.TotpSecret;
import com.github.nramc.dev.journey.api.web.resources.rest.doc.RestDocCommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static com.github.nramc.dev.journey.api.web.resources.Resources.MY_SECURITY_ATTRIBUTE_TOTP;

@RestController
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Manage User Security TOTP Settings Resource")
@Validated
public class TotpResource {
    private final UserDetailsService userDetailsService;
    private final TotpService totpService;

    // 1. new key
    @Operation(summary = "Generate QR Code with new TOTP secret key")
    @RestDocCommonResponse
    @ApiResponse(responseCode = "200", description = "new totp secret key")
    @GetMapping(value = MY_SECURITY_ATTRIBUTE_TOTP, produces = MediaType.APPLICATION_JSON_VALUE)
    public QRImageDetails generateTotp(Authentication authentication) {
        AuthUser authUser = (AuthUser) userDetailsService.loadUserByUsername(authentication.getName());
        return totpService.newQRCodeData(authUser);
    }

    // 2. activate totp
    @Operation(summary = "Verify Code for the new secret key and activate TOTP 2FA for the user")
    @RestDocCommonResponse
    @ApiResponse(responseCode = "200", description = "2FA TOTP activated")
    @PostMapping(value = MY_SECURITY_ATTRIBUTE_TOTP, consumes = MediaType.APPLICATION_JSON_VALUE)
    public void activateTotp(
            @RequestBody @Valid TotpActivationRequest activationRequest,
            Authentication authentication) {
        AuthUser authUser = (AuthUser) userDetailsService.loadUserByUsername(authentication.getName());
        totpService.activateTotp(authUser,
                TotpCode.valueOf(activationRequest.code()),
                TotpSecret.valueOf(activationRequest.secretKey())
        );
    }


    // 3. deactivate totp

    // 4. verify totp

}
