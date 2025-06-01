package com.github.nramc.dev.journey.api.core.security.webauthn;

import com.github.nramc.dev.journey.api.core.utils.NoCodeCoverageGenerated;
import com.github.nramc.dev.journey.api.repository.user.AuthUser;
import com.yubico.webauthn.data.PublicKeyCredentialCreationOptions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Slf4j
@NoCodeCoverageGenerated
public class InMemoryPublicKeyCredentialCreationOptionRepository implements PublicKeyCredentialCreationOptionRepository {
    private static final Map<String, PublicKeyCredentialCreationOptions> store = new HashMap<>();

    public void save(AuthUser user, PublicKeyCredentialCreationOptions options) {
        store.put(user.getUsername(), options);
        log.info("Saved PublicKeyCredentialCreationOptions for user: {}", user.getUsername());
    }

    public PublicKeyCredentialCreationOptions get(AuthUser user) {
        PublicKeyCredentialCreationOptions options = store.get(user.getUsername());
        log.info("Retrieved PublicKeyCredentialCreationOptions for user: {} exists:{}", user.getUsername(), options != null);
        return options;
    }

    public void delete(AuthUser user) {
        store.remove(user.getUsername());
        log.info("Deleted PublicKeyCredentialCreationOptions for user: {}", user.getUsername());
    }

}
