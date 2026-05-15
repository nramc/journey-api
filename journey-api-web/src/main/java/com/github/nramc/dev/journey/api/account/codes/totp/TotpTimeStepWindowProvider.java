package com.github.nramc.dev.journey.api.account.codes.totp;

import lombok.RequiredArgsConstructor;

import java.time.Instant;

@RequiredArgsConstructor
public class TotpTimeStepWindowProvider {
    private final TotpProperties totpProperties;

    public long provide() {
        return Math.floorDiv(Instant.now().getEpochSecond(), totpProperties.timeStepSizeInSeconds());
    }
}
