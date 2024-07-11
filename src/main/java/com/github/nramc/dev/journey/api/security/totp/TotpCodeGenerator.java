package com.github.nramc.dev.journey.api.security.totp;

import com.github.nramc.dev.journey.api.security.totp.config.TotpProperties;
import com.github.nramc.dev.journey.api.security.totp.model.TotpCode;
import com.github.nramc.dev.journey.api.security.totp.model.TotpSecret;
import com.github.nramc.dev.journey.api.web.exceptions.NonTechnicalException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@RequiredArgsConstructor
@Slf4j
public class TotpCodeGenerator {
    private final TotpProperties properties;
    private final TotpTimeStepWindowProvider timeStepWindowProvider;

    public TotpCode generate(TotpSecret totpSecret) {
        try {
            long timeStepWindow = timeStepWindowProvider.provide();
            byte[] key = Base64.getDecoder().decode(totpSecret.secret());
            byte[] data = ByteBuffer.allocate(8).putLong(timeStepWindow).array();

            Mac mac = Mac.getInstance(properties.totpAlgorithm().getHmacAlgorithm());
            mac.init(new SecretKeySpec(key, properties.totpAlgorithm().getHmacAlgorithm()));
            byte[] hash = mac.doFinal(data);

            int offset = hash[hash.length - 1] & 0xF;
            int binary = ((hash[offset] & 0x7F) << 24) | ((hash[offset + 1] & 0xFF) << 16) | ((hash[offset + 2] & 0xFF) << 8) | (hash[offset + 3] & 0xFF);
            int otp = binary % (int) Math.pow(10, properties.numberOfDigits());

            return TotpCode.valueOf(StringUtils.leftPad(String.valueOf(otp), properties.numberOfDigits(), "0"));
        } catch (NoSuchAlgorithmException | InvalidKeyException ex) {
            log.error(ex.getMessage(), ex);
            throw new NonTechnicalException(ex.getMessage());
        }

    }

}
