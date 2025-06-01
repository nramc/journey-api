package com.github.nramc.dev.journey.api.repository.user.credential;

import com.yubico.webauthn.RegisteredCredential;
import com.yubico.webauthn.data.ByteArray;
import com.yubico.webauthn.data.PublicKeyCredentialDescriptor;

public final class UserPublicKeyCredentialEntityConverter {
    private UserPublicKeyCredentialEntityConverter() {
        throw new IllegalStateException("Utility class");
    }

    public static PublicKeyCredentialDescriptor toPublicKeyCredentialDescriptor(UserPublicKeyCredentialEntity entity) {
        return PublicKeyCredentialDescriptor.builder()
                .id(ByteArray.fromBase64(entity.getCredentialId()))
                .build();
    }

    public static RegisteredCredential toRegisteredCredential(UserPublicKeyCredentialEntity entity) {
        return RegisteredCredential.builder()
                .credentialId(ByteArray.fromBase64(entity.getCredentialId()))
                .userHandle(ByteArray.fromBase64(entity.getUserHandle()))
                .publicKeyCose(ByteArray.fromBase64(entity.getPublicKeyCose()))
                .signatureCount(entity.getSignatureCount())
                .build();
    }
}
