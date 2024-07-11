package com.github.nramc.dev.journey.api.security.totp;

import com.github.nramc.dev.journey.api.security.totp.config.TotpProperties;
import com.github.nramc.dev.journey.api.security.totp.model.TotpCode;
import com.github.nramc.dev.journey.api.security.totp.model.TotpSecret;
import lombok.experimental.UtilityClass;

@UtilityClass
public class TotpDemo {


    public static void main(String[] args) {
        TotpProperties totpProperties = TotpProperties.builder()
                .totpAlgorithm(TotpAlgorithm.SHA1)
                .numberOfDigits(6)
                .timeStepSizeInSeconds(30)
                .secretLength(32)
                .build();
        TotpSecretGenerator secretGenerator = new TotpSecretGenerator(totpProperties);
        TotpTimeStepWindowProvider totpTimeStepWindowProvider = new TotpTimeStepWindowProvider(totpProperties);
        TotpCodeGenerator totpCodeGenerator = new TotpCodeGenerator(totpProperties, totpTimeStepWindowProvider);
        TotpCodeVerifier totpCodeVerifier = new TotpCodeVerifier(totpCodeGenerator);

        // Example secret key (base32 encoded)
        TotpSecret secretKey = secretGenerator.generate();
        System.out.println("Secret Key generated: " + secretKey.secret());

        // Generate current TOTP
        TotpCode totp = totpCodeGenerator.generate(secretKey);
        System.out.println("Generated TOTP: " + totp.code());

        // Verify the TOTP (example)
        boolean isValid = totpCodeVerifier.verify(secretKey, totp);
        System.out.println("Is TOTP valid: " + isValid);
    }


}
