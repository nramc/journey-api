package com.github.nramc.dev.journey.api.utils;

import com.github.nramc.dev.journey.api.models.core.EmailAddress;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EmailAddressObfuscatorTest {

    @Test
    void obfuscate_whenEmailAddressNull_shouldReturnEmptyStringGracefully() {
        assertEquals("", EmailAddressObfuscator.obfuscate(null), "should be null safe");
    }

    @Test
    void obfuscate_WhenEmailEmpty_shouldReturnEmptyStringGracefully() {
        assertEquals("", EmailAddressObfuscator.obfuscate(""), "should be null safe");
    }

    @Test
    void obfuscate_WhenEmailAddressBlank_shouldReturnEmptyStringGracefully() {
        assertEquals("", EmailAddressObfuscator.obfuscate("  "), "should be null safe");
    }

    @Test
    void obfuscate_WhenEmailAddressValidAndComplete_shouldProvideCompleteObfuscatedValue() {
        assertEquals("f*****************@gmail.com",
                EmailAddressObfuscator.obfuscate("firstname.lastname@gmail.com"));
    }

    @Test
    void obfuscate_WhenEmailLocalPartNotExists_shouldPerformObfuscationGracefullyForExistingPart() {
        assertEquals("@gmail.com",
                EmailAddressObfuscator.obfuscate("@gmail.com"));
    }

    @Test
    void obfuscate_WhenEmailNonLocalPartNotExists_shouldPerformObfuscationGracefullyForExistingPart() {
        assertEquals("f*****************@",
                EmailAddressObfuscator.obfuscate("firstname.lastname"));
    }

    @Test
    void obfuscate_whenEmailAddressModelToStringMethodCalled_shouldReturnObfuscatedString() {
        assertEquals("EmailAddress{value='f*****************@gmail.com'}", EmailAddress.valueOf("firstname.lastname@gmail.com").toString());
    }

}
