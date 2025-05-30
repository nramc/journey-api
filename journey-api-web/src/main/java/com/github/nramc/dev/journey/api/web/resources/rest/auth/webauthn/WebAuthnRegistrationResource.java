package com.github.nramc.dev.journey.api.web.resources.rest.auth.webauthn;

import com.github.nramc.dev.journey.api.core.security.webauthn.WebAuthnService;
import com.github.nramc.dev.journey.api.repository.user.AuthUser;
import com.yubico.webauthn.data.PublicKeyCredentialCreationOptions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/webauthn/register")
@RequiredArgsConstructor
@Slf4j
public class WebAuthnRegistrationResource {
    private final UserDetailsService userDetailsService;
    private final WebAuthnService webAuthnService;

    /**
     * Starts the WebAuthn registration process for the authenticated user.
     *
     * @param userDetails the authenticated user details
     * @return a ResponseEntity containing PublicKeyCredentialCreationOptions
     */
    @PostMapping("/start")
    public ResponseEntity<PublicKeyCredentialCreationOptions> startRegistration(@AuthenticationPrincipal AuthUser userDetails) {
        userDetails = (AuthUser) userDetailsService.loadUserByUsername(userDetails.getUsername());
        PublicKeyCredentialCreationOptions options = webAuthnService.startRegistration(userDetails);
        return ResponseEntity.ok(options);
    }
}