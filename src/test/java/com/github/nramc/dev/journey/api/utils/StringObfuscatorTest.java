package com.github.nramc.dev.journey.api.utils;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StringObfuscatorTest {

    @ParameterizedTest
    @CsvSource({
            "Test, T***",
            "Test Test, T********",
            "Test123, T******",
    })
    void obfuscate_WhenNumberOfVisibleCharactersNotSpecified_shouldConsiderDefaultVisibleCharacters(String string, String expectedObfuscatedString) {
        assertEquals(expectedObfuscatedString, StringObfuscator.obfuscate(string));
    }

    @ParameterizedTest
    @NullAndEmptySource
    void obfuscate_whenStringNull_shouldReturnEmptyStringGracefully(String string) {
        assertEquals("", StringObfuscator.obfuscate(string));
    }

    @ParameterizedTest
    @CsvSource({
            "f256b0f8-ab84-401d-ac45-3d027514c537, f256b0f8****************************",
            "4ddead68-d3f8-4c75-89d3-7efffd2f83ea, 4ddead68****************************"
    })
    void obfuscate_whenNumberOfVisibleCharactersSpecified_shouldConsiderThem(String string, String expectedObfuscatedString) {
        int numberOfVisibleCharacters = 8;
        assertEquals(expectedObfuscatedString, StringObfuscator.obfuscate(string, numberOfVisibleCharacters));
    }

    @ParameterizedTest
    @NullAndEmptySource
    void obfuscate_WhenNumberOfVisibleCharactersSpecified_butStringValueIsNullOrBlank_shouldReturnEmptyValueGracefully(String string) {
        int numberOfVisibleCharacters = 8;
        assertEquals("", StringObfuscator.obfuscate(string, numberOfVisibleCharacters));
    }

}
