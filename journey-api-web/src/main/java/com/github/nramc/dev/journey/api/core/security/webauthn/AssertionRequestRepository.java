package com.github.nramc.dev.journey.api.core.security.webauthn;

import com.yubico.webauthn.AssertionRequest;
import com.yubico.webauthn.data.ByteArray;

public interface AssertionRequestRepository {
    void save(ByteArray userHandle, AssertionRequest request);

    AssertionRequest get(ByteArray userHandle);

    void delete(ByteArray userHandle);
}
