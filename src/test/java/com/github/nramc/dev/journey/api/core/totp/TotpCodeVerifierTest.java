package com.github.nramc.dev.journey.api.core.totp;

import com.github.nramc.dev.journey.api.core.usecase.code.TotpCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class TotpCodeVerifierTest {
    private static final TotpSecret TOTP_SECRET = TotpSecret.valueOf("E6DCVTM46CLPRXOE6NNNXCPWAIR3L5QZ");
    private static final long CURRENT_TIME_ASSUMPTION = 1720742401;
    private static final TotpProperties TOTP_PROPERTIES = TotpProperties.builder()
            .totpAlgorithm(TotpAlgorithm.SHA1)
            .numberOfDigits(6)
            .timeStepSizeInSeconds(30)
            .secretLength(32)
            .maxAllowedTimeStepDiscrepancy(1)
            .build();

    @Mock
    private TotpTimeStepWindowProvider timeStepWindowProvider;
    private TotpCodeGenerator codeGenerator;
    private TotpCodeVerifier codeVerifier;

    @BeforeEach
    void setup() {
        Mockito.when(timeStepWindowProvider.provide())
                .thenReturn(Math.floorDiv(CURRENT_TIME_ASSUMPTION, TOTP_PROPERTIES.timeStepSizeInSeconds()));
        codeGenerator = new TotpCodeGenerator(TOTP_PROPERTIES, timeStepWindowProvider);
        codeVerifier = new TotpCodeVerifier(TOTP_PROPERTIES, codeGenerator, timeStepWindowProvider);
    }

    @ParameterizedTest
    @MethodSource
    void verify_whenCodeCreatedWithinAllowedWindowSize_shouldAccepted(long codeCreatedTimeAtMills) {
        long currentTimeWindow = Math.floorDiv(codeCreatedTimeAtMills, TOTP_PROPERTIES.timeStepSizeInSeconds());
        TotpCode codeCreatedAtCurrentTimeWindow = codeGenerator.generate(TOTP_SECRET, currentTimeWindow);

        boolean result = codeVerifier.verify(TOTP_SECRET, codeCreatedAtCurrentTimeWindow);

        assertThat(result).isTrue();
    }

    static Stream<Arguments> verify_whenCodeCreatedWithinAllowedWindowSize_shouldAccepted() {
        return Stream.of(
                // Acceptable step window
                Arguments.of(CURRENT_TIME_ASSUMPTION),
                Arguments.of(Instant.ofEpochSecond(CURRENT_TIME_ASSUMPTION).minusSeconds(10).getEpochSecond()),
                Arguments.of(Instant.ofEpochSecond(CURRENT_TIME_ASSUMPTION).minusSeconds(20).getEpochSecond()),
                Arguments.of(Instant.ofEpochSecond(CURRENT_TIME_ASSUMPTION).minusSeconds(30).getEpochSecond())
        );
    }

    @ParameterizedTest
    @MethodSource
    void verify_whenCodeCreatedOutOfAllowedWindowSize_shouldBeRejected(long codeCreatedTimeAtMills) {
        long currentTimeWindow = Math.floorDiv(codeCreatedTimeAtMills, TOTP_PROPERTIES.timeStepSizeInSeconds());
        TotpCode codeCreatedAtCurrentTimeWindow = codeGenerator.generate(TOTP_SECRET, currentTimeWindow);

        boolean result = codeVerifier.verify(TOTP_SECRET, codeCreatedAtCurrentTimeWindow);

        assertThat(result).isFalse();
    }

    static Stream<Arguments> verify_whenCodeCreatedOutOfAllowedWindowSize_shouldBeRejected() {
        return Stream.of(
                // NonAcceptable windows
                Arguments.of(Instant.ofEpochSecond(CURRENT_TIME_ASSUMPTION).minusSeconds(40).getEpochSecond()),
                Arguments.of(Instant.ofEpochSecond(CURRENT_TIME_ASSUMPTION).minusSeconds(50).getEpochSecond()),
                Arguments.of(Instant.ofEpochSecond(CURRENT_TIME_ASSUMPTION).minusSeconds(60).getEpochSecond()),
                Arguments.of(Instant.ofEpochSecond(CURRENT_TIME_ASSUMPTION).minusSeconds(70).getEpochSecond()),
                Arguments.of(Instant.ofEpochSecond(CURRENT_TIME_ASSUMPTION).minusSeconds(80).getEpochSecond()),
                Arguments.of(Instant.ofEpochSecond(CURRENT_TIME_ASSUMPTION).minusSeconds(90).getEpochSecond()),
                Arguments.of(Instant.ofEpochSecond(CURRENT_TIME_ASSUMPTION).minusSeconds(100).getEpochSecond())
        );
    }

}
