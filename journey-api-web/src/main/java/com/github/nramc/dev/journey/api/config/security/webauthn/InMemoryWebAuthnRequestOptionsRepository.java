package com.github.nramc.dev.journey.api.config.security.webauthn;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.web.webauthn.api.PublicKeyCredentialRequestOptions;
import org.springframework.security.web.webauthn.authentication.PublicKeyCredentialRequestOptionsRepository;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
public class InMemoryWebAuthnRequestOptionsRepository implements PublicKeyCredentialRequestOptionsRepository {
    private final Map<String, PublicKeyCredentialRequestOptions> optionsStore = new HashMap<>();

    @Override
    public void save(HttpServletRequest request, HttpServletResponse response, PublicKeyCredentialRequestOptions options) {
        String username = Optional.ofNullable(request.getUserPrincipal()).map(Principal::getName).orElseThrow();
        optionsStore.put(username, options);
        log.debug("Saved WebAuthn request options for user: {} with options:{}", username, options);
    }

    @Override
    public PublicKeyCredentialRequestOptions load(HttpServletRequest request) {
        String username = Optional.ofNullable(request.getUserPrincipal()).map(Principal::getName).orElseThrow();
        PublicKeyCredentialRequestOptions options = optionsStore.getOrDefault(username, null);
        log.debug("Loading WebAuthn request options for user: {} with options:{}", username, options);
        return options;
    }
}
