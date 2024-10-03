package com.github.nramc.dev.journey.api.utils;

import com.github.nramc.dev.journey.api.core.domain.EmailAddress;
import com.github.nramc.dev.journey.api.core.utils.EmailAddressObfuscator;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EmailAddressObfuscatorTest {

    @Test
    void obfuscate_whenEmailAddressNull_shouldReturnEmptyStringGracefully() {
        assertThat(EmailAddressObfuscator.obfuscate(null)).as("should be null safe").isEmpty();
    }

    @Test
    void obfuscate_WhenEmailEmpty_shouldReturnEmptyStringGracefully() {
        assertThat(EmailAddressObfuscator.obfuscate("")).as("should be null safe").isEmpty();
    }

    @Test
    void obfuscate_WhenEmailAddressBlank_shouldReturnEmptyStringGracefully() {
        assertThat(EmailAddressObfuscator.obfuscate("  ")).as("should be null safe").isEmpty();
    }

    @Test
    void obfuscate_WhenEmailAddressValidAndComplete_shouldProvideCompleteObfuscatedValue() {
        assertThat(EmailAddressObfuscator.obfuscate("firstname.lastname@gmail.com")).isEqualTo("f*****************@gmail.com");
    }

    @Test
    void obfuscate_WhenEmailLocalPartNotExists_shouldPerformObfuscationGracefullyForExistingPart() {
        assertThat(EmailAddressObfuscator.obfuscate("@gmail.com")).isEqualTo("@gmail.com");
    }

    @Test
    void obfuscate_WhenEmailNonLocalPartNotExists_shouldPerformObfuscationGracefullyForExistingPart() {
        assertThat(EmailAddressObfuscator.obfuscate("firstname.lastname")).isEqualTo("f*****************@");
    }

    @Test
    void obfuscate_whenEmailAddressModelToStringMethodCalled_shouldReturnObfuscatedString() {
        assertThat(EmailAddress.valueOf("firstname.lastname@gmail.com")).hasToString("EmailAddress{value='f*****************@gmail.com'}");
    }

}
