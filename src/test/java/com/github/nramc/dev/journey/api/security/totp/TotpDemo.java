package com.github.nramc.dev.journey.api.security.totp;

import com.github.nramc.dev.journey.api.web.resources.rest.users.security.attributes.totp.TotpAlgorithm;
import com.github.nramc.dev.journey.api.web.resources.rest.users.security.attributes.totp.TotpCodeGenerator;
import com.github.nramc.dev.journey.api.web.resources.rest.users.security.attributes.totp.TotpCodeVerifier;
import com.github.nramc.dev.journey.api.web.resources.rest.users.security.attributes.totp.TotpSecretGenerator;
import com.github.nramc.dev.journey.api.web.resources.rest.users.security.attributes.totp.TotpTimeStepWindowProvider;
import com.github.nramc.dev.journey.api.web.resources.rest.users.security.attributes.totp.config.TotpProperties;
import com.github.nramc.dev.journey.api.web.resources.rest.users.security.attributes.totp.model.TotpCode;
import com.github.nramc.dev.journey.api.web.resources.rest.users.security.attributes.totp.model.TotpSecret;
import lombok.experimental.UtilityClass;

@UtilityClass
public class TotpDemo {


    public static void main(String[] args) {
        TotpProperties totpProperties = TotpProperties.builder()
                .totpAlgorithm(TotpAlgorithm.SHA1)
                .numberOfDigits(6)
                .timeStepSizeInSeconds(30)
                .secretLength(32)
                .maxAllowedTimeStepDiscrepancy(1)
                .build();
        TotpSecretGenerator secretGenerator = new TotpSecretGenerator(totpProperties);
        TotpTimeStepWindowProvider totpTimeStepWindowProvider = new TotpTimeStepWindowProvider(totpProperties);
        TotpCodeGenerator totpCodeGenerator = new TotpCodeGenerator(totpProperties, totpTimeStepWindowProvider);
        TotpCodeVerifier totpCodeVerifier = new TotpCodeVerifier(totpProperties, totpCodeGenerator, totpTimeStepWindowProvider);

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
