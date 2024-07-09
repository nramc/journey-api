package com.github.nramc.dev.journey.api.security.totp;

import com.github.nramc.dev.journey.api.security.totp.config.TotpProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TotpSecretGeneratorTest {
    private static final TotpProperties PROPERTIES = TotpProperties.builder()
            .numberOfDigits(6)
            .secretLength(32)
            .build();

    @Test
    void testSecretGenerated() {
        TotpSecretGenerator generator = new TotpSecretGenerator(PROPERTIES);
        String secret = generator.generate();
        assertNotNull(secret);
        assertFalse(secret.isEmpty());
    }

    @ParameterizedTest
    @ValueSource(ints = {16, 32, 64, 128, 256, 512, 1024, 2048, 4096})
    void testCharacterLengths(int keyLength) {
        TotpSecretGenerator generator = new TotpSecretGenerator(
                PROPERTIES.toBuilder().secretLength(keyLength).build());
        String secret = generator.generate();
        assertEquals(keyLength, secret.length());
    }

    @Test
    void testValidBase32Encoded() {
        TotpSecretGenerator generator = new TotpSecretGenerator(PROPERTIES);
        String secret = generator.generate();

        // Test the string contains only A-Z, 2-7 with optional ending =s
        assertTrue(secret.matches("^[A-Z2-7]+=*$"));

        // And the length must be a multiple of 8
        assertEquals(0, secret.length() % 8);
    }

}