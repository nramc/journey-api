package com.github.nramc.dev.journey.api.repository.user;

import com.github.nramc.dev.journey.api.core.security.webauthn.PublicKeyCredentialRepository;
import com.github.nramc.dev.journey.api.repository.user.credential.UserPublicKeyCredentialEntity;
import com.github.nramc.dev.journey.api.repository.user.credential.UserPublicKeyCredentialEntityConverter;
import com.github.nramc.dev.journey.api.repository.user.credential.UserPublicKeyCredentialRepository;
import com.yubico.webauthn.RegisteredCredential;
import com.yubico.webauthn.RegistrationResult;
import com.yubico.webauthn.data.ByteArray;
import com.yubico.webauthn.data.PublicKeyCredentialDescriptor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class PersistencePublicKeyCredentialRepository implements PublicKeyCredentialRepository {
    private final UserPublicKeyCredentialRepository credentialRepository;

    /**
     * Add a new credential for the user with the given username and user handle.
     *
     * <p>Implementations of this method MUST NOT return null.
     *
     * @param username   the username of the user to whom the credential is being added
     * @param userHandle the user handle of the user to whom the credential is being added
     * @param credential the credential to be added
     */
    @Override
    public void addCredential(String username, ByteArray userHandle, RegisteredCredential credential) {
        UserPublicKeyCredentialEntity entity = UserPublicKeyCredentialEntity.builder()
                .username(username)
                .userHandle(userHandle.getBase64())
                .credentialId(credential.getCredentialId().getBase64())
                .publicKeyCose(credential.getPublicKeyCose().getBase64())
                .signatureCount(credential.getSignatureCount())
                .build();
        credentialRepository.save(entity);
        log.info("Added credential for user: {}, credential ID: {}", username, credential.getCredentialId());

    }

    /**
     * Remove the credential with the given credential ID for the user with the given username.
     *
     * <p>Implementations of this method MUST NOT return null.
     *
     * @param username     the username of the user from whom the credential is being removed
     * @param credentialId the Credential ID of the credential to be removed
     */
    @Override
    public void removeCredential(String username, ByteArray credentialId) {
        credentialRepository.deleteByUsernameAndCredentialId(username, credentialId.getBase64());
        log.info("Removed credential for user: {}, credential ID: {}", username, credentialId);
    }

    /**
     * Get the credential IDs of all credentials registered to the user with the given username.
     *
     * <p>After a successful registration ceremony, the {@link RegistrationResult#getKeyId()} method
     * returns a value suitable for inclusion in this set.
     *
     * <p>Implementations of this method MUST NOT return null.
     * Reference:
     * <a href="https://github.com/Yubico/java-webauthn-server/blob/main/webauthn-server-demo/src/main/java/demo/webauthn/InMemoryRegistrationStorage.java>InMemoryRegistrationStorage.java</a>
     *
     * @param username the username of the user whose credentials are being queried
     */
    @Override
    public Set<PublicKeyCredentialDescriptor> getCredentialIdsForUsername(String username) {
        Set<PublicKeyCredentialDescriptor> result = credentialRepository.findByUsername(username)
                .stream()
                .map(UserPublicKeyCredentialEntityConverter::toPublicKeyCredentialDescriptor)
                .collect(Collectors.toSet());

        log.info("Retrieved {} credential IDs for user: {}", result.size(), username);
        return result;
    }

    /**
     * Get the user handle corresponding to the given username - the inverse of {@link
     * #getUsernameForUserHandle(ByteArray)}.
     *
     * <p>Used to look up the user handle based on the username, for authentication ceremonies where
     * the username is already given.
     *
     * <p>Implementations of this method MUST NOT return null.
     *
     * @param username the username of the user whose handle is being queried
     */
    @Override
    public Optional<ByteArray> getUserHandleForUsername(String username) {
        List<UserPublicKeyCredentialEntity> credentials = credentialRepository.findByUsername(username);
        if (credentials.isEmpty()) {
            log.info("No user handle found for username: {}", username);
            return Optional.empty();
        }
        log.info("Found user handle for username: {}", username);
        return Optional.of(credentials.getFirst().getUserHandle()).map(ByteArray::fromBase64);
    }

    /**
     * Get the username corresponding to the given user handle - the inverse of {@link
     * #getUserHandleForUsername(String)}.
     *
     * <p>Used to look up the username based on the user handle, for username-less authentication
     * ceremonies.
     *
     * <p>Implementations of this method MUST NOT return null.
     *
     * @param userHandle the user handle of the user whose username is being queried
     */
    @Override
    public Optional<String> getUsernameForUserHandle(ByteArray userHandle) {
        Optional<UserPublicKeyCredentialEntity> entity = credentialRepository.findByUserHandle(userHandle.getBase64());
        if (entity.isPresent()) {
            String username = entity.get().getUsername();
            log.info("Found username: {} for user handle: {}", username, userHandle);
            return Optional.of(username);
        }
        log.info("No username found for user handle: {}", userHandle);
        return Optional.empty();
    }

    /**
     * Look up the public key and stored signature count for the given credential registered to the
     * given user.
     *
     * <p>The returned {@link RegisteredCredential} is not expected to be long-lived. It may be read
     * directly from a database or assembled from other components.
     *
     * <p>Implementations of this method MUST NOT return null.
     *
     * @param credentialId the Credential ID which is being queried
     * @param userHandle   the user handle of the user whose credential is being queried
     */
    @Override
    public Optional<RegisteredCredential> lookup(ByteArray credentialId, ByteArray userHandle) {
        Optional<UserPublicKeyCredentialEntity> entity = credentialRepository.findByCredentialIdAndUserHandle(credentialId.getBase64(), userHandle.getBase64());
        if (entity.isPresent()) {
            log.info("Found credential with ID: {} for user handle: {}", credentialId, userHandle);
            return entity.map(UserPublicKeyCredentialEntityConverter::toRegisteredCredential);
        }
        log.info("No credential found with ID: {} for user handle: {}", credentialId, userHandle);
        return Optional.empty();
    }

    /**
     * Look up all credentials with the given credential ID, regardless of what user they're
     * registered to.
     *
     * <p>This is used to refuse registration of duplicate credential IDs. Therefore, under normal
     * circumstances this method should only return zero or one credential (this is an expected
     * consequence, not an interface requirement).
     *
     * <p>Implementations of this method MUST NOT return null.
     *
     * @param credentialId the Credential ID which is being queried
     */
    @Override
    public Set<RegisteredCredential> lookupAll(ByteArray credentialId) {
        List<UserPublicKeyCredentialEntity> entities = credentialRepository.findByCredentialId(credentialId.getBase64());
        if (entities.isEmpty()) {
            log.info("No credentials found with ID: {}", credentialId);
            return Set.of();
        }
        log.info("Found {} credentials with ID: {}", entities.size(), credentialId);
        return entities.stream().map(UserPublicKeyCredentialEntityConverter::toRegisteredCredential).collect(Collectors.toSet());
    }
}
