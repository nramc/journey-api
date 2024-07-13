package com.github.nramc.dev.journey.api.web.resources.rest.users.security.totp;

import com.github.nramc.dev.journey.api.repository.auth.AuthUser;
import com.github.nramc.dev.journey.api.services.totp.TotpService;
import com.github.nramc.dev.journey.api.web.resources.rest.doc.RestDocCommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.github.nramc.dev.journey.api.web.resources.Resources.MY_SECURITY_ATTRIBUTE_TOTP;

@RestController
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Manage User Security TOTP Settings Resource")
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

    // 3. deactivate totp

    // 4. verify totp

}
