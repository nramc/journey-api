package com.github.nramc.dev.journey.api.core.security.webauthn;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class WebAuthnUtilsTest {

    @Test
    void testNewUserHandle() {
        // Arrange & Act
        var userHandle = WebAuthnUtils.newUserHandle();

        // Assert
        assertNotNull(userHandle);
        assertEquals(64, userHandle.getBytes().length);
    }

}
