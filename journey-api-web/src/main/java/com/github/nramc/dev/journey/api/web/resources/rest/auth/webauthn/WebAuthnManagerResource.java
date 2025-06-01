package com.github.nramc.dev.journey.api.web.resources.rest.auth.webauthn;

import com.github.nramc.dev.journey.api.core.security.webauthn.WebAuthnService;
import com.github.nramc.dev.journey.api.repository.user.AuthUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/webauthn/credentials")
public class WebAuthnManagerResource {
    private final UserDetailsService userDetailsService;
    private final WebAuthnService webAuthnService;

    @GetMapping
    public List<CredentialInfo> listCredentials(Authentication authentication) {
        AuthUser userDetails = (AuthUser) userDetailsService.loadUserByUsername(authentication.getName());
        return webAuthnService.listCredentials(userDetails).stream()
                .map(CredentialInfo::from)
                .toList();
    }

    @DeleteMapping
    public void deleteCredential(Authentication authentication, @RequestParam String credentialId) {
        AuthUser userDetails = (AuthUser) userDetailsService.loadUserByUsername(authentication.getName());
        webAuthnService.deleteCredential(userDetails, credentialId);
    }
}
