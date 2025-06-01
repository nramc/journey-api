package com.github.nramc.dev.journey.api.web.resources.rest.auth.webauthn;

import com.github.nramc.dev.journey.api.core.security.webauthn.StoredCredentialInformation;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder(toBuilder = true)
public record CredentialInfo(String credentialId, String userHandle, String name, LocalDateTime createdAt) {

    public static CredentialInfo from(StoredCredentialInformation credential) {
        return CredentialInfo.builder()
                .credentialId(credential.credential().getCredentialId().getBase64())
                .userHandle(credential.credential().getUserHandle().getBase64())
                .name(credential.metadata().name())
                .createdAt(credential.metadata().createdAt())
                .build();
    }
}
