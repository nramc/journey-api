package com.github.nramc.dev.journey.api.web.resources.rest.users.security.attributes.totp;

import com.github.nramc.dev.journey.api.web.resources.rest.users.security.attributes.totp.config.TotpProperties;
import com.github.nramc.dev.journey.api.web.resources.rest.users.security.confirmationcode.TotpCode;
import com.github.nramc.dev.journey.api.web.resources.rest.users.security.attributes.totp.model.TotpSecret;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.stream.IntStream;

@RequiredArgsConstructor
@Slf4j
public class TotpCodeVerifier {
    private final TotpProperties totpProperties;
    private final TotpCodeGenerator codeGenerator;
    private final TotpTimeStepWindowProvider timeStepWindowProvider;

    public boolean verify(TotpSecret secret, TotpCode code) {

        return IntStream.range(-totpProperties.maxAllowedTimeStepDiscrepancy(), totpProperties.maxAllowedTimeStepDiscrepancy())
                .boxed()
                .sorted(Collections.reverseOrder())
                .map(value -> timeStepWindowProvider.provide() + value)
                .map(timeStepWindow -> codeGenerator.generate(secret, timeStepWindow))
                .anyMatch(expectedCode -> StringUtils.equals(expectedCode.code(), code.code()));
    }

}
