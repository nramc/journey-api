package com.github.nramc.dev.journey.api.web.resources.rest.users.security.attributes.totp;

import com.github.nramc.dev.journey.api.web.resources.rest.users.security.attributes.totp.config.TotpProperties;
import lombok.RequiredArgsConstructor;

import java.time.Instant;

@RequiredArgsConstructor
public class TotpTimeStepWindowProvider {
    private final TotpProperties totpProperties;

    public long provide() {
        return Math.floorDiv(Instant.now().getEpochSecond(), totpProperties.timeStepSizeInSeconds());
    }
}
