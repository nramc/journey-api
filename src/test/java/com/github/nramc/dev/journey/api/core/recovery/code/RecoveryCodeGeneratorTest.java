package com.github.nramc.dev.journey.api.core.recovery.code;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class RecoveryCodeGeneratorTest {
    private RecoveryCodeGenerator recoveryCodeGenerator;

    @BeforeEach
    void setUp() {
        this.recoveryCodeGenerator = new RecoveryCodeGenerator();
    }

    @Test
    void generate_whenNumberOfCodesGiven_shouldGenerateCodes() {
        List<String> codes = recoveryCodeGenerator.generate(16);
        assertThat(codes).isNotEmpty().hasSize(16)
                .allMatch(StringUtils::isNotBlank);
    }

    @Test
    void generate_whenCodesGenerated_shouldMatchCharacters() {
        List<String> codes = recoveryCodeGenerator.generate(16);
        assertThat(codes).isNotEmpty().hasSize(16)
                .allMatch(StringUtils::isNotBlank)
                .allMatch(code -> code.matches("[a-z0-9]{4}-[a-z0-9]{4}-[a-z0-9]{4}-[a-z0-9]{4}"));
    }

    @Test
    void generate_whenCodesGenerated_shouldBeUnique() {
        List<String> codes = recoveryCodeGenerator.generate(25);
        assertThat(codes.stream().distinct().count()).isEqualTo(25);
    }

    @Test
    void testInvalidNumberThrowsException() {
        List<String> codes = recoveryCodeGenerator.generate(-1);
        assertThat(codes).isNullOrEmpty();
    }
}