package com.github.nramc.dev.journey.api.security.totp;

import com.github.nramc.dev.journey.api.security.totp.config.TotpProperties;
import lombok.RequiredArgsConstructor;

import java.time.Instant;

@RequiredArgsConstructor
public class TotpTimeStepWindowProvider {
    private final TotpProperties totpProperties;

    public long provide() {
        return Instant.now().getEpochSecond() / totpProperties.timeStepSizeInSeconds();
    }
}
