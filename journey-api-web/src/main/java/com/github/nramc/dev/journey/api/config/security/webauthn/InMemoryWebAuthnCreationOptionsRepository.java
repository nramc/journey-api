package com.github.nramc.dev.journey.api.config.security.webauthn;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.web.webauthn.api.PublicKeyCredentialCreationOptions;
import org.springframework.security.web.webauthn.registration.PublicKeyCredentialCreationOptionsRepository;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
public class InMemoryWebAuthnCreationOptionsRepository implements PublicKeyCredentialCreationOptionsRepository {
    private final Map<String, PublicKeyCredentialCreationOptions> optionsStore = new HashMap<>();

    @Override
    public void save(HttpServletRequest request, HttpServletResponse response, PublicKeyCredentialCreationOptions options) {
        String username = Optional.of(request.getUserPrincipal()).map(Principal::getName).orElseThrow();
        optionsStore.put(username, options);
        log.debug("Saving creation options for user:{} with options:{}", username, options);
    }

    @Override
    public PublicKeyCredentialCreationOptions load(HttpServletRequest request) {
        String username = Optional.of(request.getUserPrincipal()).map(Principal::getName).orElseThrow();
        PublicKeyCredentialCreationOptions options = optionsStore.get(username);
        log.debug("Loading creation options for user:{} with options:{}", username, options);
        return options;
    }
}
