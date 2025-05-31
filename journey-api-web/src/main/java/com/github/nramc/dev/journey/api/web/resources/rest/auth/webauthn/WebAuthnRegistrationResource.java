package com.github.nramc.dev.journey.api.web.resources.rest.auth.webauthn;

import com.github.nramc.dev.journey.api.core.security.webauthn.WebAuthnService;
import com.github.nramc.dev.journey.api.repository.user.AuthUser;
import com.yubico.webauthn.data.AuthenticatorAttestationResponse;
import com.yubico.webauthn.data.ClientRegistrationExtensionOutputs;
import com.yubico.webauthn.data.PublicKeyCredential;
import com.yubico.webauthn.data.PublicKeyCredentialCreationOptions;
import com.yubico.webauthn.exception.RegistrationFailedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

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

    /**
     * Completes the WebAuthn registration process for the authenticated user.
     *
     * @param userDetails             the authenticated user details
     * @param publicKeyCredentialJson the JSON representation of the PublicKeyCredential
     * @return a ResponseEntity indicating success or failure
     */
    @PostMapping("/finish")
    public ResponseEntity<Void> finishRegistration(@AuthenticationPrincipal AuthUser userDetails, @RequestBody String publicKeyCredentialJson)
            throws IOException, RegistrationFailedException {
        userDetails = (AuthUser) userDetailsService.loadUserByUsername(userDetails.getUsername());
        PublicKeyCredential<AuthenticatorAttestationResponse, ClientRegistrationExtensionOutputs> publicKeyCredential =
                PublicKeyCredential.parseRegistrationResponseJson(publicKeyCredentialJson);
        webAuthnService.finishRegistration(userDetails, publicKeyCredential);
        return ResponseEntity.ok().build();
    }
}