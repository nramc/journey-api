package com.github.nramc.dev.journey.api.core.utils;

import org.apache.commons.lang3.StringUtils;

public final class StringObfuscator {
    private static final int NUM_VISIBLE_CHARS_AT_BEGIN = 1;

    private StringObfuscator() {
    }

    public static String obfuscate(String string) {
        return obfuscate(string, NUM_VISIBLE_CHARS_AT_BEGIN);
    }

    public static String obfuscate(String string, int numberOfVisibleCharacters) {
        if (StringUtils.isBlank(string)) {
            return "";
        } else {
            final String toBeReplaced = StringUtils.substring(string, numberOfVisibleCharacters, string.length());
            return getVisibleCharacters(string, numberOfVisibleCharacters) + obfuscatedCharacters(toBeReplaced);
        }
    }

    private static String obfuscatedCharacters(String toBeReplaced) {
        return StringUtils.repeat("*", toBeReplaced.length());
    }

    private static String getVisibleCharacters(String string, int numberOfVisibleCharacters) {
        return StringUtils.substring(string, 0, numberOfVisibleCharacters);
    }
}
