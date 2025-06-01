package com.github.nramc.dev.journey.api.web.resources.rest.auth.webauthn;

import com.github.nramc.dev.journey.api.core.security.webauthn.StoredCredentialInformation;
import com.github.nramc.dev.journey.api.core.utils.NoCodeCoverageGenerated;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder(toBuilder = true)
@NoCodeCoverageGenerated
public record CredentialInfo(String credentialId, String userHandle, String name, LocalDateTime createdAt, String deviceInfo) {

    public static CredentialInfo from(StoredCredentialInformation credential) {
        return CredentialInfo.builder()
                .credentialId(credential.credential().getCredentialId().getBase64())
                .userHandle(credential.credential().getUserHandle().getBase64())
                .name(credential.metadata().name())
                .createdAt(credential.metadata().createdAt())
                .deviceInfo(credential.metadata().deviceInfo())
                .build();
    }
}
