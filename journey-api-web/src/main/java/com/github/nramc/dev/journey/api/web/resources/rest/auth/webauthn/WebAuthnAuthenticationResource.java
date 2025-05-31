package com.github.nramc.dev.journey.api.web.resources.rest.auth.webauthn;

import com.github.nramc.dev.journey.api.core.security.webauthn.WebAuthnService;
import com.github.nramc.dev.journey.api.repository.user.AuthUser;
import com.github.nramc.dev.journey.api.web.resources.rest.auth.dto.LoginResponse;
import com.github.nramc.dev.journey.api.web.resources.rest.auth.provider.JwtResponseProvider;
import com.yubico.webauthn.AssertionResult;
import com.yubico.webauthn.data.AuthenticatorAssertionResponse;
import com.yubico.webauthn.data.ClientAssertionExtensionOutputs;
import com.yubico.webauthn.data.PublicKeyCredential;
import com.yubico.webauthn.data.PublicKeyCredentialRequestOptions;
import com.yubico.webauthn.exception.AssertionFailedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/webauthn/authenticate")
@RequiredArgsConstructor
@Slf4j
public class WebAuthnAuthenticationResource {
    private final WebAuthnService webAuthnService;
    private final UserDetailsService userDetailsService;
    private final JwtResponseProvider jwtResponseProvider;

    /**
     * Starts the WebAuthn authentication process for the given username.
     *
     * @param username the username to authenticate
     * @return PublicKeyCredentialRequestOptions for the frontend
     */
    @PostMapping("/start")
    public ResponseEntity<PublicKeyCredentialRequestOptions> startAuthentication(@RequestParam String username) {
        PublicKeyCredentialRequestOptions options = webAuthnService.startAssertion(username);
        return ResponseEntity.ok(options);
    }

    /**
     * Completes the WebAuthn authentication process.
     *
     * @param publicKeyCredentialJson the JSON representation of the PublicKeyCredential
     * @return ResponseEntity indicating success or failure
     */
    @PostMapping("/finish")
    public ResponseEntity<LoginResponse> finishAuthentication(@RequestBody String publicKeyCredentialJson)
            throws IOException, AssertionFailedException {

        PublicKeyCredential<AuthenticatorAssertionResponse, ClientAssertionExtensionOutputs> publicKeyCredential =
                PublicKeyCredential.parseAssertionResponseJson(publicKeyCredentialJson);
        AssertionResult assertionResult = webAuthnService.finishAssertion(publicKeyCredential);

        if (!assertionResult.isSuccess()) {
            log.error("WebAuthn authentication failed: {}", assertionResult.getUsername());
            return ResponseEntity.badRequest().build();
        }

        AuthUser userDetails = (AuthUser) userDetailsService.loadUserByUsername(assertionResult.getUsername());
        return ResponseEntity.ok(jwtResponseProvider.jwtResponse(userDetails));
    }
}
