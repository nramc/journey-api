package com.github.nramc.dev.journey.api.core.security.webauthn;

import com.yubico.webauthn.data.ByteArray;

import java.security.SecureRandom;
import java.util.Random;

public final class WebAuthnUtils {
    private static final Random random = new SecureRandom();

    private WebAuthnUtils() {
        // Utility class should not be instantiated
    }

    public static ByteArray newUserHandle() {
        byte[] userHandle = new byte[64];
        random.nextBytes(userHandle);
        return new ByteArray(userHandle);
    }
}
