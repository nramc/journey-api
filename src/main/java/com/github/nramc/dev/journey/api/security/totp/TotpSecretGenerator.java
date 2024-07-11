package com.github.nramc.dev.journey.api.security.totp;

import com.github.nramc.dev.journey.api.security.totp.config.TotpProperties;
import com.github.nramc.dev.journey.api.security.totp.model.TotpSecret;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.binary.Base32;

import java.security.SecureRandom;

@RequiredArgsConstructor
public class TotpSecretGenerator {
    private final SecureRandom randomBytes = new SecureRandom();
    private static final Base32 encoder = new Base32();
    private final TotpProperties properties;

    public TotpSecret generate() {
        return TotpSecret.valueOf(new String(encoder.encode(getRandomBytes())));
    }

    private byte[] getRandomBytes() {
        // 5 bits per char in base32
        byte[] bytes = new byte[(properties.secretLength() * 5) / 8];
        randomBytes.nextBytes(bytes);

        return bytes;
    }
}
