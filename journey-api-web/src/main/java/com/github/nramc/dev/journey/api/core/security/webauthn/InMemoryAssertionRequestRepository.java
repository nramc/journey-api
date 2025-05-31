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
     * Saves the AssertionRequest associated with the given userHandle.
     *
     * @param userHandle the unique identifier for the user
     * @param request    the AssertionRequest to save
     */
    @Override
    public void save(ByteArray userHandle, AssertionRequest request) {
        requireNonNull(userHandle, "User handle must not be null");
        store.put(userHandle, request);
        log.info("Saved AssertionRequest for userHandle: {}", userHandle);
    }

    /**
     * Retrieves the AssertionRequest associated with the given userHandle.
     *
     * @param userHandle the unique identifier for the user
     * @return the AssertionRequest if found, otherwise null
     */
    @Override
    public AssertionRequest get(ByteArray userHandle) {
        requireNonNull(userHandle, "User handle must not be null");
        AssertionRequest request = store.get(userHandle);
        log.info("Retrieved AssertionRequest for userHandle: {} exists:{}", userHandle, request != null);
        return request;
    }

    /**
     * Deletes the AssertionRequest associated with the given userHandle.
     *
     * @param userHandle the unique identifier for the user
     */
    @Override
    public void delete(ByteArray userHandle) {
        requireNonNull(userHandle, "User handle must not be null");
        store.remove(userHandle);
        log.info("Deleted AssertionRequest for userHandle: {}", userHandle);
    }
}
