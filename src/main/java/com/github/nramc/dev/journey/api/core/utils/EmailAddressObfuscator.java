package com.github.nramc.dev.journey.api.core.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.Optional;

/**
 * Obfuscates an email address by masking local part (username) except the first character.
 * <p>
 * For example the email address <c>name.surname@gmail.com</c> will be obfuscated as <c>n***********@gmail.com</c>.
 * </p>
 */
public final class EmailAddressObfuscator {
    private static final String EMAIL_ADDRESS_SEPARATOR = "@";
    private static final int NO_VISIBLE_LOCAL_PART_CHARS = 1;
    private static final String MASK_CHAR = "*";


    private EmailAddressObfuscator() {
    }

    private static String getNonLocalPart(String emailAddress) {
        return StringUtils.substringAfterLast(emailAddress, EMAIL_ADDRESS_SEPARATOR);
    }

    private static String getLocalPart(String emailAddress) {
        return StringUtils.substringBeforeLast(emailAddress, EMAIL_ADDRESS_SEPARATOR);
    }

    private static String obfuscateLocalPart(String emailAddress) {
        String localPart = getLocalPart(emailAddress);
        return StringUtils.substring(localPart, 0, NO_VISIBLE_LOCAL_PART_CHARS) +
                StringUtils.repeat(MASK_CHAR, StringUtils.length(localPart) - NO_VISIBLE_LOCAL_PART_CHARS);
    }

    private static String getObfuscateValue(String emailAddress) {
        return obfuscateLocalPart(emailAddress) + EMAIL_ADDRESS_SEPARATOR + getNonLocalPart(emailAddress);
    }


    public static String obfuscate(final String emailAddress) {
        return Optional.ofNullable(emailAddress)
                .filter(StringUtils::isNotBlank)
                .map(EmailAddressObfuscator::getObfuscateValue).orElse("");
    }

}
