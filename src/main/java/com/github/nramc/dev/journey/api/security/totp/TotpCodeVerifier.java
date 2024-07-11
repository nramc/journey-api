package com.github.nramc.dev.journey.api.security.totp;

import com.github.nramc.dev.journey.api.security.totp.model.TotpCode;
import com.github.nramc.dev.journey.api.security.totp.model.TotpSecret;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@RequiredArgsConstructor
public class TotpCodeVerifier {
    private final TotpCodeGenerator codeGenerator;

    public boolean verify(TotpSecret secret, TotpCode code) {
        TotpCode expectedCode = codeGenerator.generate(secret);
        return StringUtils.equals(expectedCode.code(), code.code());
    }

}
