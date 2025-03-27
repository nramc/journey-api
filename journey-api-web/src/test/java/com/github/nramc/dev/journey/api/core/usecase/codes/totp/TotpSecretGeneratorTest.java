package com.github.nramc.dev.journey.api.core.usecase.codes.totp;

import com.github.nramc.dev.journey.api.core.domain.user.settings.security.TotpSecret;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class TotpSecretGeneratorTest {
    private static final TotpProperties PROPERTIES = TotpProperties.builder()
            .numberOfDigits(6)
            .secretLength(32)
            .build();

    @Test
    void secretGenerated() {
        TotpSecretGenerator generator = new TotpSecretGenerator(PROPERTIES);
        TotpSecret secret = generator.generate();
        assertThat(secret).isNotNull()
                .extracting(TotpSecret::secret)
                .asString().isNotBlank().hasSize(PROPERTIES.secretLength());
    }

    @ParameterizedTest
    @ValueSource(ints = {16, 32, 64, 128, 256, 512, 1024, 2048, 4096})
    void characterLengths(int keyLength) {
        TotpSecretGenerator generator = new TotpSecretGenerator(
                PROPERTIES.toBuilder().secretLength(keyLength).build());
        TotpSecret secret = generator.generate();
        assertThat(secret.secret()).hasSize(keyLength);
    }

    @Test
    void validBase32Encoded() {
        TotpSecretGenerator generator = new TotpSecretGenerator(PROPERTIES);
        TotpSecret secret = generator.generate();

        // Test the string contains only A-Z, 2-7 with optional ending =s
        assertThat(secret.secret()).matches("^[A-Z2-7]+=*$");

        // And the length must be a multiple of 8
        assertThat(secret.secret().length() % 8).isZero();
    }

}
