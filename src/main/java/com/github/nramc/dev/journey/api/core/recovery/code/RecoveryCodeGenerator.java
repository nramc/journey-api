package com.github.nramc.dev.journey.api.core.recovery.code;

import java.security.SecureRandom;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

public class RecoveryCodeGenerator {

    // Recovery code must reach a minimum entropy to be secured
    //   code entropy = log( {characters-count} ^ {code-length} ) / log(2)
    // the settings used below allows the code to reach an entropy of 82 bits :
    //  log(36^16) / log(2) == 82.7...

    // Recovery code must be simple to read and enter by end user when needed :
    //  - generate a code composed of numbers and lower case characters from latin alphabet (36 possible characters)
    //  - split code in groups separated with dash for better readability, for example 4ckn-xspn-et8t-xgr0
    private static final char[] CHARACTERS = "abcdefghijklmnopqrstuvwxyz0123456789".toCharArray();
    private static final int CODE_LENGTH = 16;
    private static final int GROUPS_NBR = 4;

    private final Random random = new SecureRandom();

    public List<String> generate(int numberOfRecoveryCodes) {
        return IntStream.range(0, numberOfRecoveryCodes).boxed().map(index -> generateCode()).toList();
    }

    private String generateCode() {
        final StringBuilder code = new StringBuilder(CODE_LENGTH + (CODE_LENGTH / GROUPS_NBR) - 1);

        for (int i = 0; i < CODE_LENGTH; i++) {
            // Append random character from authorized ones
            code.append(CHARACTERS[random.nextInt(CHARACTERS.length)]);

            // Split code into groups for increased readability
            if ((i + 1) % GROUPS_NBR == 0 && (i + 1) != CODE_LENGTH) {
                code.append("-");
            }
        }

        return code.toString();
    }

}
