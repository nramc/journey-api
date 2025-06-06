package com.github.nramc.dev.journey.api.web.resources.rest.auth.webauthn;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.nramc.dev.journey.api.core.security.webauthn.WebAuthnService;
import com.github.nramc.dev.journey.api.core.utils.HttpUtils;
import com.github.nramc.dev.journey.api.repository.user.AuthUser;
import com.yubico.webauthn.data.AuthenticatorAttestationResponse;
import com.yubico.webauthn.data.ClientRegistrationExtensionOutputs;
import com.yubico.webauthn.data.PublicKeyCredential;
import com.yubico.webauthn.data.PublicKeyCredentialCreationOptions;
import com.yubico.webauthn.exception.RegistrationFailedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
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
     * @param authentication the authentication object containing user details
     * @return PublicKeyCredentialCreationOptions for the frontend
     * @throws JsonProcessingException if there is an error processing JSON
     */
    @PostMapping("/start")
    public String startRegistration(Authentication authentication) throws JsonProcessingException {
        AuthUser userDetails = (AuthUser) userDetailsService.loadUserByUsername(authentication.getName());
        PublicKeyCredentialCreationOptions options = webAuthnService.startRegistration(userDetails);
        return options.toCredentialsCreateJson();
    }

    /**
     * Completes the WebAuthn registration process for the authenticated user.
     *
     * @param authentication          the authentication object containing user details
     * @param publicKeyCredentialJson the JSON representation of the PublicKeyCredential
     * @return a ResponseEntity indicating success or failure
     */
    @PostMapping("/finish")
    public ResponseEntity<Void> finishRegistration(
            Authentication authentication,
            @RequestBody String publicKeyCredentialJson,
            @RequestHeader("User-Agent") String userAgent)
            throws IOException, RegistrationFailedException {
        AuthUser userDetails = (AuthUser) userDetailsService.loadUserByUsername(authentication.getName());
        HttpClientRequestInfo requestInfo = HttpClientRequestInfo.builder()
                .deviceInfo(HttpUtils.extractDeviceInfo(userAgent, userDetails.getName()))
                .build();
        PublicKeyCredential<AuthenticatorAttestationResponse, ClientRegistrationExtensionOutputs> publicKeyCredential =
                PublicKeyCredential.parseRegistrationResponseJson(publicKeyCredentialJson);
        webAuthnService.finishRegistration(userDetails, publicKeyCredential, requestInfo);
        return ResponseEntity.ok().build();
    }
}
