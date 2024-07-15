package com.github.nramc.dev.journey.api.security.totp;

import com.github.nramc.dev.journey.api.security.totp.config.TotpProperties;
import com.github.nramc.dev.journey.api.security.totp.model.TotpCode;
import com.github.nramc.dev.journey.api.security.totp.model.TotpSecret;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TotpCodeGeneratorTest {
    private static final TotpProperties TOTP_PROPERTIES = TotpProperties.builder()
            .numberOfDigits(6)
            .secretLength(32)
            .totpAlgorithm(TotpAlgorithm.SHA1)
            .timeStepSizeInSeconds(30)
            .maxAllowedTimeStepDiscrepancy(1)
            .build();

    private TotpCode generateCode(TotpAlgorithm algorithm, TotpSecret secret, int time, int codeLength) {
        TotpProperties properties = TOTP_PROPERTIES.toBuilder()
                .totpAlgorithm(algorithm)
                .numberOfDigits(codeLength)
                .build();
        TotpTimeStepWindowProvider timeStepWindowProvider = mock(TotpTimeStepWindowProvider.class);
        when(timeStepWindowProvider.provide()).thenReturn(time / properties.timeStepSizeInSeconds());
        TotpCodeGenerator codeGenerator = new TotpCodeGenerator(properties, timeStepWindowProvider);
        return codeGenerator.generate(secret);
    }

    static Stream<Arguments> expectedCodesProvider() {
        return Stream.of(
                arguments("W3C5B3WKR4AUKFVWYU2WNMYB756OAKWY", 1567631536, TotpAlgorithm.SHA1, "082371"),
                arguments("W3C5B3WKR4AUKFVWYU2WNMYB756OAKWY", 1567631536, TotpAlgorithm.SHA256, "272978"),
                arguments("W3C5B3WKR4AUKFVWYU2WNMYB756OAKWY", 1567631536, TotpAlgorithm.SHA512, "325200"),

                arguments("makrzl2hict4ojeji2iah4kndmq6sgka", 1582750403, TotpAlgorithm.SHA1, "848586"),
                arguments("makrzl2hict4ojeji2iah4kndmq6sgka", 1582750403, TotpAlgorithm.SHA256, "965726"),
                arguments("makrzl2hict4ojeji2iah4kndmq6sgka", 1582750403, TotpAlgorithm.SHA512, "741306")
        );
    }

    @ParameterizedTest
    @MethodSource("expectedCodesProvider")
    void generate_whenDataValid_shouldGenerateCodeAsExpected(TotpSecret secret, int time, TotpAlgorithm algorithm, String expectedCode) {
        TotpCode totpCode = generateCode(algorithm, secret, time, 6);
        assertThat(totpCode).isNotNull().extracting(TotpCode::code).isEqualTo(expectedCode);
    }

    @ParameterizedTest
    @ValueSource(ints = {4, 6, 8})
    void generate_whenCodeLengthProvided_shouldGenerateCodeAsExpected(int codeLength) {
        TotpCode totpCode = generateCode(TotpAlgorithm.SHA1, TotpSecret.valueOf("W3C5B3WKR4AUKFVWYU2WNMYB756OAKWY"), 1567631536, codeLength);
        assertThat(totpCode).isNotNull().extracting(TotpCode::code).asString().hasSize(codeLength);
    }

}
