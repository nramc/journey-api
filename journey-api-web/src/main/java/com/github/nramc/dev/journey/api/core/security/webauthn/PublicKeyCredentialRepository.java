package com.github.nramc.dev.journey.api.core.security.webauthn;

import com.yubico.webauthn.CredentialRepository;
import com.yubico.webauthn.RegisteredCredential;
import com.yubico.webauthn.data.ByteArray;

import java.util.List;

public interface PublicKeyCredentialRepository extends CredentialRepository {
    /**
     * Adds a credential to the repository.
     *
     * @param credentialMetadata metadata about the credential being added, including username and user handle
     * @param credential         the credential to add
     */
    void addCredential(RegisteredCredential credential, CredentialMetadata credentialMetadata);

    List<StoredCredentialInformation> getCredentials(String username);

    /**
     * Removes a credential from the repository.
     *
     * @param username     the username associated with the credential
     * @param credentialId the ID of the credential to remove
     */
    void removeCredential(String username, ByteArray credentialId);
}
