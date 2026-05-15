package com.github.nramc.dev.journey.api.account.webauthn;

import com.yubico.webauthn.RegisteredCredential;

public record StoredCredentialInformation(RegisteredCredential credential, CredentialMetadata metadata) {
}
