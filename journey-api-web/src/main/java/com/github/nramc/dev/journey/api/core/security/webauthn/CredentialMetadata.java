package com.github.nramc.dev.journey.api.core.security.webauthn;

import com.yubico.webauthn.data.ByteArray;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder(toBuilder = true)
public record CredentialMetadata(
        String username,
        ByteArray userHandle,
        LocalDateTime createdAt,
        String name) {
}
