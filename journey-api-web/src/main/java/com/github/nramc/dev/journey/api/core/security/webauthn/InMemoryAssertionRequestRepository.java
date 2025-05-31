package com.github.nramc.dev.journey.api.core.security.webauthn;

import com.yubico.webauthn.AssertionRequest;
import com.yubico.webauthn.data.ByteArray;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Objects.requireNonNull;

@Slf4j
@RequiredArgsConstructor
public class InMemoryAssertionRequestRepository implements AssertionRequestRepository {
    private final Map<ByteArray, AssertionRequest> store = new ConcurrentHashMap<>();

    /**
     * Saves the AssertionRequest associated with the given challenge.
     *
     * @param challenge the unique identifier for the assertion request
     * @param request   the AssertionRequest to save
     */
    @Override
    public void save(ByteArray challenge, AssertionRequest request) {
        requireNonNull(challenge, "Challenge must not be null");
        store.put(challenge, request);
        log.info("Saved AssertionRequest for challenge: {}", challenge);
    }

    /**
     * Retrieves the AssertionRequest associated with the given challenge.
     *
     * @param challenge the unique identifier for the user
     * @return the AssertionRequest if found, otherwise null
     */
    @Override
    public AssertionRequest get(ByteArray challenge) {
        requireNonNull(challenge, "Challenge to look up must not be null");
        AssertionRequest request = store.get(challenge);
        log.info("Retrieved AssertionRequest for challenge: {} exists:{}", challenge, request != null);
        return request;
    }

    /**
     * Deletes the AssertionRequest associated with the given challenge.
     *
     * @param challenge the unique identifier for the assertion request
     */
    @Override
    public void delete(ByteArray challenge) {
        requireNonNull(challenge, "Challenge must not be null");
        store.remove(challenge);
        log.info("Deleted AssertionRequest for challenge: {}", challenge);
    }
}
