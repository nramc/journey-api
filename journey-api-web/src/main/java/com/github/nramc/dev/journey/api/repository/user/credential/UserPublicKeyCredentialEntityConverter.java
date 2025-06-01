package com.github.nramc.dev.journey.api.repository.user.credential;

import com.github.nramc.dev.journey.api.core.security.webauthn.CredentialMetadata;
import com.github.nramc.dev.journey.api.core.security.webauthn.StoredCredentialInformation;
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

    public static StoredCredentialInformation toRegisteredCredential(UserPublicKeyCredentialEntity entity) {
        RegisteredCredential credential = RegisteredCredential.builder()
                .credentialId(ByteArray.fromBase64(entity.getCredentialId()))
                .userHandle(ByteArray.fromBase64(entity.getUserHandle()))
                .publicKeyCose(ByteArray.fromBase64(entity.getPublicKeyCose()))
                .signatureCount(entity.getSignatureCount())
                .build();
        CredentialMetadata metadata = CredentialMetadata.builder()
                .username(entity.getUsername())
                .userHandle(ByteArray.fromBase64(entity.getUserHandle()))
                .name(entity.getName())
                .createdAt(entity.getCreatedAt())
                .build();
        return new StoredCredentialInformation(credential, metadata);
    }
}
