package com.github.nramc.dev.journey.api.core.security.webauthn;

import com.yubico.webauthn.RegisteredCredential;
import com.yubico.webauthn.RegistrationResult;
import com.yubico.webauthn.data.ByteArray;
import com.yubico.webauthn.data.PublicKeyCredentialDescriptor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/*
 * todo:
 *  - add Javadoc
 * - refactor to use a database instead of in-memory storage
 * - refactor to use streams for better readability
 *
 * */

@Slf4j
public class InMemoryCredentialRepository implements PublicKeyCredentialRepository {
    private final Map<String, List<RegisteredCredential>> credentialsByUsername = new ConcurrentHashMap<>();
    private final Map<ByteArray, String> usernameByUserHandle = new ConcurrentHashMap<>();


    @Override
    public void addCredential(String username, ByteArray userHandle, RegisteredCredential credential) {
        credentialsByUsername.computeIfAbsent(username, k -> new ArrayList<>()).add(credential);
        usernameByUserHandle.putIfAbsent(userHandle, username);
        log.info("Added credential for user: {}, credential ID: {}", username, credential.getCredentialId());
    }

    @Override
    public void removeCredential(String username, ByteArray credentialId) {
        List<RegisteredCredential> credentials = credentialsByUsername.get(username);
        if (credentials != null) {
            credentials.removeIf(credential -> credential.getCredentialId().equals(credentialId));
            if (credentials.isEmpty()) {
                credentialsByUsername.remove(username);
            }
        }
        log.info("Removed credential for user: {}, credential ID: {}", username, credentialId);
    }

    /**
     * Get the credential IDs of all credentials registered to the user with the given username.
     *
     * <p>After a successful registration ceremony, the {@link RegistrationResult#getKeyId()} method
     * returns a value suitable for inclusion in this set.
     *
     * <p>Implementations of this method MUST NOT return null.
     * <p>
     * Reference:
     * <a href="https://github.com/Yubico/java-webauthn-server/blob/main/webauthn-server-demo/src/main/java/demo/webauthn/InMemoryRegistrationStorage.java>InMemoryRegistrationStorage.java</a>
     *
     * @param username the username of the user whose credentials are being queried
     */
    @Override
    public Set<PublicKeyCredentialDescriptor> getCredentialIdsForUsername(String username) {
        List<RegisteredCredential> credentials = credentialsByUsername.getOrDefault(username, Collections.emptyList());
        Set<PublicKeyCredentialDescriptor> result = new HashSet<>();
        for (RegisteredCredential cred : credentials) {
            result.add(PublicKeyCredentialDescriptor.builder()
                    .id(cred.getCredentialId())
                    .transports(Set.of())
                    .build());
        }
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
        List<RegisteredCredential> credentials = credentialsByUsername.get(username);
        if (credentials != null && !credentials.isEmpty()) {
            log.info("Found user handle for username: {}", username);
            return Optional.of(credentials.getFirst().getUserHandle());
        }
        log.info("No user handle found for username: {}", username);
        return Optional.empty();
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
        log.info("Looking up username for user handle: {}", userHandle);
        return Optional.ofNullable(usernameByUserHandle.get(userHandle));
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
        String username = usernameByUserHandle.get(userHandle);
        if (username != null) {
            List<RegisteredCredential> credentials = credentialsByUsername.get(username);
            if (credentials != null) {
                log.info("Looking up credential with ID: {} for user: {}", credentialId, username);
                return credentials.stream()
                        .filter(c -> c.getCredentialId().equals(credentialId))
                        .findFirst();
            }
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
        Set<RegisteredCredential> result = new HashSet<>();
        for (List<RegisteredCredential> credentials : credentialsByUsername.values()) {
            credentials.stream()
                    .filter(c -> c.getCredentialId().equals(credentialId))
                    .forEach(result::add);
        }
        log.info("Found {} credentials with ID: {}", result.size(), credentialId);
        return result;
    }
}
