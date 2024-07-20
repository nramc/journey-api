package com.github.nramc.dev.journey.api.security.totp;

import com.github.nramc.dev.journey.api.security.totp.config.TotpProperties;
import com.github.nramc.dev.journey.api.security.totp.model.TotpCode;
import com.github.nramc.dev.journey.api.security.totp.model.TotpSecret;
import com.github.nramc.dev.journey.api.web.exceptions.NonTechnicalException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base32;
import org.apache.commons.lang3.StringUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@RequiredArgsConstructor
@Slf4j
public class TotpCodeGenerator {
    private final TotpProperties properties;
    private final TotpTimeStepWindowProvider timeStepWindowProvider;

    public TotpCode generate(TotpSecret totpSecret) {
        long timeStepWindow = timeStepWindowProvider.provide();
        return generate(totpSecret, timeStepWindow);
    }

    public TotpCode generate(TotpSecret totpSecret, long timeStepWindow) {
        try {
            byte[] hash = generateHash(totpSecret.secret(), timeStepWindow);
            return TotpCode.valueOf(getDigitsFromHash(hash));
        } catch (NoSuchAlgorithmException | InvalidKeyException ex) {
            throw new NonTechnicalException("Could not generate TotpCode:" + ex.getMessage());
        }
    }

    private byte[] generateHash(String key, long counter) throws NoSuchAlgorithmException, InvalidKeyException {
        byte[] data = new byte[8];
        long value = counter;
        for (int i = 8; i-- > 0; value >>>= 8) {
            data[i] = (byte) value;
        }

        Base32 codec = new Base32();
        byte[] decodedKey = codec.decode(key);
        SecretKeySpec signKey = new SecretKeySpec(decodedKey, properties.totpAlgorithm().getHmacAlgorithm());
        Mac mac = Mac.getInstance(properties.totpAlgorithm().getHmacAlgorithm());
        mac.init(signKey);

        return mac.doFinal(data);
    }

    private String getDigitsFromHash(byte[] hash) {
        int offset = hash[hash.length - 1] & 0xF;

        long truncatedHash = 0;

        for (int i = 0; i < 4; ++i) {
            truncatedHash <<= 8;
            truncatedHash |= hash[offset + i] & 0xFF;
        }

        truncatedHash &= 0x7FFFFFFF;
        truncatedHash %= (long) Math.pow(10L, properties.numberOfDigits());

        // Left pad with 0s for a n-digit code
        return StringUtils.leftPad(Long.toString(truncatedHash), properties.numberOfDigits(), '0');
    }

}
