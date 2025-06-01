package com.github.nramc.dev.journey.api.web.resources.rest.auth.webauthn;

import com.yubico.webauthn.data.ByteArray;
import lombok.Builder;

@Builder(toBuilder = true)
public record CredentialInfo(String credentialId, String userHandle) {

    public static CredentialInfo of(ByteArray credentialId, ByteArray userHandle) {
        return new CredentialInfo(credentialId.getBase64(), userHandle.getBase64());
    }
}
