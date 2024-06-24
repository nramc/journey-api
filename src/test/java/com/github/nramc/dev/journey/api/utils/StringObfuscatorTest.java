package com.github.nramc.dev.journey.api.utils;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import static org.assertj.core.api.Assertions.assertThat;

class StringObfuscatorTest {

    @ParameterizedTest
    @CsvSource({
            "Test, T***",
            "Test Test, T********",
            "Test123, T******",
    })
    void obfuscate_WhenNumberOfVisibleCharactersNotSpecified_shouldConsiderDefaultVisibleCharacters(String string, String expectedObfuscatedString) {
        assertThat(StringObfuscator.obfuscate(string)).isEqualTo(expectedObfuscatedString);
    }

    @ParameterizedTest
    @NullAndEmptySource
    void obfuscate_whenStringNull_shouldReturnEmptyStringGracefully(String string) {
        assertThat(StringObfuscator.obfuscate(string)).isEmpty();
    }

    @ParameterizedTest
    @CsvSource({
            "f256b0f8-ab84-401d-ac45-3d027514c537, f256b0f8****************************",
            "4ddead68-d3f8-4c75-89d3-7efffd2f83ea, 4ddead68****************************"
    })
    void obfuscate_whenNumberOfVisibleCharactersSpecified_shouldConsiderThem(String string, String expectedObfuscatedString) {
        int numberOfVisibleCharacters = 8;
        assertThat(StringObfuscator.obfuscate(string, numberOfVisibleCharacters)).isEqualTo(expectedObfuscatedString);
    }

    @ParameterizedTest
    @NullAndEmptySource
    void obfuscate_WhenNumberOfVisibleCharactersSpecified_butStringValueIsNullOrBlank_shouldReturnEmptyValueGracefully(String string) {
        int numberOfVisibleCharacters = 8;
        assertThat(StringObfuscator.obfuscate(string, numberOfVisibleCharacters)).isEmpty();
    }

}
