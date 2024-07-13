package com.github.nramc.dev.journey.api.services.totp;

import com.github.nramc.dev.journey.api.repository.auth.AuthUser;
import com.github.nramc.dev.journey.api.security.totp.QRCodeGenerator;
import com.github.nramc.dev.journey.api.security.totp.TotpSecretGenerator;
import com.github.nramc.dev.journey.api.security.totp.config.TotpProperties;
import com.github.nramc.dev.journey.api.security.totp.model.QRCodeData;
import com.github.nramc.dev.journey.api.security.totp.model.TotpSecret;
import com.github.nramc.dev.journey.api.web.resources.rest.users.security.totp.QRImageDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class TotpService {
    private final TotpProperties totpProperties;
    private final TotpSecretGenerator secretGenerator;
    private final QRCodeGenerator qrCodeGenerator;

    public QRImageDetails newQRCodeData(AuthUser authUser) {
        TotpSecret secret = secretGenerator.generate();

        QRCodeData qrCodeData = toQRCodeData(secret, authUser);
        byte[] qrImageData = qrCodeGenerator.generate(qrCodeData);

        return QRImageDetails.builder()
                .secretKey(secret.secret())
                .data(qrImageData)
                .build();
    }

    private QRCodeData toQRCodeData(TotpSecret secret, AuthUser authUser) {
        return QRCodeData.builder()
                .type(totpProperties.qrType())
                .algorithm(totpProperties.totpAlgorithm().getHmacAlgorithm())
                .digits(totpProperties.numberOfDigits())
                .period(totpProperties.timeStepSizeInSeconds())
                .issuer(totpProperties.qrIssuer())
                .width(totpProperties.qrWidth())
                .height(totpProperties.qrHeight())
                .label(authUser.getUsername())
                .secret(secret.secret())
                .build();
    }

}
