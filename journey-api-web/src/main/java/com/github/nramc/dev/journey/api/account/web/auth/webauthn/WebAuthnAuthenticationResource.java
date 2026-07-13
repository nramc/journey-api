package com.github.nramc.dev.journey.api.account.web.auth.webauthn;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.nramc.dev.journey.api.account.repository.AuthUser;
import com.github.nramc.dev.journey.api.account.web.auth.dto.LoginResponse;
import com.github.nramc.dev.journey.api.account.web.auth.provider.JwtResponseProvider;
import com.github.nramc.dev.journey.api.account.webauthn.WebAuthnService;
import com.yubico.webauthn.AssertionResult;
import com.yubico.webauthn.data.AuthenticatorAssertionResponse;
import com.yubico.webauthn.data.ClientAssertionExtensionOutputs;
import com.yubico.webauthn.data.PublicKeyCredential;
import com.yubico.webauthn.data.PublicKeyCredentialRequestOptions;
import com.yubico.webauthn.exception.AssertionFailedException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Tag(name = "WebAuthn Authentication", description = "Authenticate using FIDO2 passkeys")
public class WebAuthnAuthenticationResource {
    private final WebAuthnService webAuthnService;
    private final UserDetailsService userDetailsService;
    private final JwtResponseProvider jwtResponseProvider;

    /**
     * Starts the WebAuthn authentication process.
     *
     * @param username optional username to scope the authentication
     * @return PublicKeyCredentialRequestOptions for the frontend
     * @throws JsonProcessingException if there is an error processing JSON
     */
    @Operation(summary = "Start passkey authentication", description = "Returns PublicKeyCredentialRequestOptions for the browser to sign with a passkey.")
    @ApiResponse(responseCode = "200", description = "Authentication options JSON")
    @PostMapping("/start")
    public String startAuthentication(
            @Parameter(description = "Optional username to scope the authentication challenge")
            @RequestParam(value = "username", required = false) String username) throws JsonProcessingException {
        PublicKeyCredentialRequestOptions options = webAuthnService.startAssertion(username);
        return options.toCredentialsGetJson();
    }

    /**
     * Completes the WebAuthn authentication process.
     *
     * @param publicKeyCredentialJson the JSON representation of the PublicKeyCredential
     * @return a LoginResponse containing the JWT token
     */
    @Operation(summary = "Finish passkey authentication", description = "Validates the passkey assertion and returns a JWT.")
    @ApiResponse(responseCode = "200", description = "Authentication successful and return JWT",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = LoginResponse.class)))
    @PostMapping("/finish")
    public LoginResponse finishAuthentication(
            @Parameter(description = "PublicKeyCredential JSON from the browser")
            @RequestBody String publicKeyCredentialJson) throws IOException, AssertionFailedException {
        PublicKeyCredential<AuthenticatorAssertionResponse, ClientAssertionExtensionOutputs> publicKeyCredential =
                PublicKeyCredential.parseAssertionResponseJson(publicKeyCredentialJson);
        AssertionResult result = webAuthnService.finishAssertion(publicKeyCredential);
        AuthUser userDetails = (AuthUser) userDetailsService.loadUserByUsername(result.getUsername());
        return jwtResponseProvider.jwtResponse(userDetails);
    }
}
