package com.github.nramc.dev.journey.api.core.usecase.code;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class RecoveryCodeUseCaseTest {
    private RecoveryCodeUseCase recoveryCodeUseCase;

    @BeforeEach
    void setUp() {
        this.recoveryCodeUseCase = new RecoveryCodeUseCase();
    }

    @Test
    void generate_whenNumberOfCodesGiven_shouldGenerateCodes() {
        List<String> codes = recoveryCodeUseCase.generate(16);
        assertThat(codes).isNotEmpty().hasSize(16)
                .allMatch(StringUtils::isNotBlank);
    }

    @Test
    void generate_whenCodesGenerated_shouldMatchCharacters() {
        List<String> codes = recoveryCodeUseCase.generate(16);
        assertThat(codes).isNotEmpty().hasSize(16)
                .allMatch(StringUtils::isNotBlank)
                .allMatch(code -> code.matches("[a-z0-9]{4}-[a-z0-9]{4}-[a-z0-9]{4}-[a-z0-9]{4}"));
    }

    @Test
    void generate_whenCodesGenerated_shouldBeUnique() {
        List<String> codes = recoveryCodeUseCase.generate(25);
        assertThat(codes.stream().distinct().count()).isEqualTo(25);
    }

    @Test
    void generate_whenNumberOfCodesInputInvalid_shouldProvideEmptyListGracefully() {
        List<String> codes = recoveryCodeUseCase.generate(-1);
        assertThat(codes).isNullOrEmpty();
    }
}
