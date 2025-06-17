package com.github.nramc.dev.journey.api.core.security.webauthn;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class WebAuthnUtilsTest {

    @Test
    void newUserHandle() {
        // Arrange & Act
        var userHandle = WebAuthnUtils.newUserHandle();

        // Assert
        assertThat(userHandle).isNotNull();
        assertThat(userHandle.getBytes()).hasSize(64);
    }

}
