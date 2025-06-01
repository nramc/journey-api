package com.github.nramc.dev.journey.api.core.security.webauthn;

import com.yubico.webauthn.RegisteredCredential;

public record StoredCredentialInformation(RegisteredCredential credential, CredentialMetadata metadata) {
}
