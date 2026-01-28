package com.github.nramc.dev.journey.api.repository.user;

import com.github.nramc.dev.journey.api.config.TestContainersConfiguration;
import com.github.nramc.dev.journey.api.core.security.webauthn.CredentialMetadata;
import com.github.nramc.dev.journey.api.repository.user.credential.UserPublicKeyCredentialRepository;
import com.yubico.webauthn.RegisteredCredential;
import com.yubico.webauthn.data.ByteArray;
import com.yubico.webauthn.data.PublicKeyCredentialDescriptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.mongodb.test.autoconfigure.DataMongoTest;
import org.springframework.context.annotation.Import;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
@Import({TestContainersConfiguration.class, PersistencePublicKeyCredentialRepository.class})
class PersistencePublicKeyCredentialRepositoryTest {
    private static final RegisteredCredential REGISTERED_CREDENTIAL = RegisteredCredential.builder()
            .credentialId(new ByteArray("test-credential-id".getBytes()))
            .userHandle(new ByteArray("test-user-handle".getBytes()))
            .publicKeyCose(new ByteArray("test-public-key-cose".getBytes()))
            .signatureCount(1)
            .build();
    private static final CredentialMetadata CREDENTIAL_METADATA = CredentialMetadata.builder()
            .username("test-user")
            .name("Test User")
            .userHandle(new ByteArray("test-user-handle".getBytes()))
            .createdAt(LocalDateTime.of(2025, 6, 1, 0, 0))
            .deviceInfo("Test Device Info")
            .build();

    @Autowired
    UserPublicKeyCredentialRepository credentialRepository;
    @Autowired
    PersistencePublicKeyCredentialRepository persistencePublicKeyCredentialRepository;

    @BeforeEach
    void setUp() {
        credentialRepository.deleteAll();
    }

    @Test
    void context() {
        assertThat(credentialRepository).isNotNull();
        assertThat(persistencePublicKeyCredentialRepository).isNotNull();
    }

    @Test
    void addCredential_whenCalled_thenShouldSaveCredential() {
        persistencePublicKeyCredentialRepository.addCredential(REGISTERED_CREDENTIAL, CREDENTIAL_METADATA);

        var credentials = credentialRepository.findByUsername(CREDENTIAL_METADATA.username());
        assertThat(credentials).isNotEmpty();
        assertThat(credentials.getFirst().getCredentialId()).isEqualTo(REGISTERED_CREDENTIAL.getCredentialId().getBase64());
        assertThat(credentials.getFirst().getUsername()).isEqualTo(CREDENTIAL_METADATA.username());
    }

    @Test
    void getCredentials_whenCalled_thenShouldReturnCredentials() {
        persistencePublicKeyCredentialRepository.addCredential(REGISTERED_CREDENTIAL, CREDENTIAL_METADATA);

        var credentials = persistencePublicKeyCredentialRepository.getCredentials(CREDENTIAL_METADATA.username());
        assertThat(credentials).isNotEmpty();
        assertThat(credentials.getFirst().credential()).isEqualTo(REGISTERED_CREDENTIAL);
        assertThat(credentials.getFirst().metadata()).isEqualTo(CREDENTIAL_METADATA);
    }

    @Test
    void removeCredential_whenCalled_thenShouldDeleteCredential() {
        persistencePublicKeyCredentialRepository.addCredential(REGISTERED_CREDENTIAL, CREDENTIAL_METADATA);

        var credentials = credentialRepository.findByUsername(CREDENTIAL_METADATA.username());
        assertThat(credentials).isNotEmpty();

        persistencePublicKeyCredentialRepository.removeCredential(CREDENTIAL_METADATA.username(), REGISTERED_CREDENTIAL.getCredentialId());

        credentials = credentialRepository.findByUsername(CREDENTIAL_METADATA.username());
        assertThat(credentials).isEmpty();
    }

    @Test
    void getCredentialIds_whenCalled_thenShouldReturnCredentialIds() {
        persistencePublicKeyCredentialRepository.addCredential(REGISTERED_CREDENTIAL, CREDENTIAL_METADATA);

        Set<PublicKeyCredentialDescriptor> credentialIds = persistencePublicKeyCredentialRepository.getCredentialIdsForUsername(CREDENTIAL_METADATA.username());
        assertThat(credentialIds).isNotEmpty().hasSize(1)
                .first().extracting(PublicKeyCredentialDescriptor::getId)
                .isEqualTo(REGISTERED_CREDENTIAL.getCredentialId());
    }

    @Test
    void getCredentialIds_whenNoCredentials_thenShouldReturnEmptySet() {
        Set<PublicKeyCredentialDescriptor> credentialIds = persistencePublicKeyCredentialRepository.getCredentialIdsForUsername("non-existent-user");
        assertThat(credentialIds).isEmpty();
    }

    @Test
    void getCredentialIds_whenMultipleCredentials_thenShouldReturnAllCredentialIds() {
        RegisteredCredential anotherCredential = RegisteredCredential.builder()
                .credentialId(new ByteArray("another-credential-id".getBytes()))
                .userHandle(new ByteArray("test-user-handle".getBytes()))
                .publicKeyCose(new ByteArray("another-public-key-cose".getBytes()))
                .signatureCount(2)
                .build();

        persistencePublicKeyCredentialRepository.addCredential(REGISTERED_CREDENTIAL, CREDENTIAL_METADATA);
        persistencePublicKeyCredentialRepository.addCredential(anotherCredential, CREDENTIAL_METADATA);

        Set<PublicKeyCredentialDescriptor> credentialIds = persistencePublicKeyCredentialRepository.getCredentialIdsForUsername(CREDENTIAL_METADATA.username());
        assertThat(credentialIds).isNotEmpty().hasSize(2)
                .extracting(PublicKeyCredentialDescriptor::getId)
                .containsExactlyInAnyOrder(REGISTERED_CREDENTIAL.getCredentialId(), anotherCredential.getCredentialId());
    }

    @Test
    void getUserHandleForUsername_whenCalled_thenShouldReturnUserHandle() {
        persistencePublicKeyCredentialRepository.addCredential(REGISTERED_CREDENTIAL, CREDENTIAL_METADATA);

        Optional<ByteArray> userHandle = persistencePublicKeyCredentialRepository.getUserHandleForUsername(CREDENTIAL_METADATA.username());
        assertThat(userHandle).isNotEmpty().hasValue(CREDENTIAL_METADATA.userHandle());
    }

    @Test
    void getUserHandleForUsername_whenNoCredentials_thenShouldReturnEmpty() {
        Optional<ByteArray> userHandle = persistencePublicKeyCredentialRepository.getUserHandleForUsername("non-existent-user");
        assertThat(userHandle).isEmpty();
    }

    @Test
    void getUsernameForUserHandle_whenCalled_thenShouldReturnUsername() {
        persistencePublicKeyCredentialRepository.addCredential(REGISTERED_CREDENTIAL, CREDENTIAL_METADATA);

        Optional<String> username = persistencePublicKeyCredentialRepository.getUsernameForUserHandle(CREDENTIAL_METADATA.userHandle());
        assertThat(username).isNotEmpty().hasValue(CREDENTIAL_METADATA.username());
    }

    @Test
    void getUsernameForUserHandle_whenNoCredentials_thenShouldReturnEmpty() {
        Optional<String> username = persistencePublicKeyCredentialRepository.getUsernameForUserHandle(new ByteArray("non-existent-user-handle".getBytes()));
        assertThat(username).isEmpty();
    }

    @Test
    void lookup_whenCalled_thenShouldReturnCredential() {
        persistencePublicKeyCredentialRepository.addCredential(REGISTERED_CREDENTIAL, CREDENTIAL_METADATA);

        Optional<RegisteredCredential> credential = persistencePublicKeyCredentialRepository.lookup(
                REGISTERED_CREDENTIAL.getCredentialId(), REGISTERED_CREDENTIAL.getUserHandle());

        assertThat(credential).isNotEmpty().hasValue(REGISTERED_CREDENTIAL);
    }

    @Test
    void lookup_whenNoCredential_thenShouldReturnEmpty() {
        Optional<RegisteredCredential> credential = persistencePublicKeyCredentialRepository.lookup(
                new ByteArray("non-existent-credential-id".getBytes()), new ByteArray("non-existent-user-handle".getBytes()));

        assertThat(credential).isEmpty();
    }

    @Test
    void lookupAll_whenCalled_thenShouldReturnAllCredentials() {
        persistencePublicKeyCredentialRepository.addCredential(REGISTERED_CREDENTIAL, CREDENTIAL_METADATA);

        Set<RegisteredCredential> credentials = persistencePublicKeyCredentialRepository.lookupAll(
                REGISTERED_CREDENTIAL.getCredentialId());

        assertThat(credentials).isNotEmpty().hasSize(1)
                .first().isEqualTo(REGISTERED_CREDENTIAL);
    }


}
