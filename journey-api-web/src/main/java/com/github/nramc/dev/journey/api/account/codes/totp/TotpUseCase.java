package com.github.nramc.dev.journey.api.account.codes.totp;

import com.github.nramc.dev.journey.api.account.repository.AuthUser;
import com.github.nramc.dev.journey.api.account.repository.attributes.UserSecurityAttributeService;
import com.github.nramc.dev.journey.api.shared.domain.user.security.TotpCode;
import com.github.nramc.dev.journey.api.shared.domain.user.security.TotpSecret;
import com.github.nramc.dev.journey.api.shared.domain.user.security.UserSecurityAttribute;
import com.github.nramc.dev.journey.api.shared.domain.user.security.UserSecurityAttributeType;
import com.github.nramc.dev.journey.api.shared.exceptions.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
public class TotpUseCase {
    private final TotpProperties totpProperties;
    private final TotpSecretGenerator secretGenerator;
    private final QRCodeGenerator qrCodeGenerator;
    private final TotpCodeVerifier codeVerifier;
    private final UserSecurityAttributeService userSecurityAttributeService;

    public QRImageDetails newQRCodeData(AuthUser authUser) {
        TotpSecret secret = secretGenerator.generate();

        QRCodeData qrCodeData = toQRCodeData(secret, authUser);
        byte[] qrImageData = qrCodeGenerator.generateWithLogo(qrCodeData);

        return QRImageDetails.builder()
                .secretKey(secret.secret())
                .data(qrImageData)
                .build();
    }

    public void activateTotp(AuthUser authUser, TotpCode code, TotpSecret secret) {
        boolean isCodeValid = codeVerifier.verify(secret, code);
        if (isCodeValid) {
            userSecurityAttributeService.saveTOTPSecret(authUser, secret);
        } else {
            throw new BusinessException("Code not valid", "totp.code.invalid");
        }
    }

    public Optional<UserSecurityAttribute> getTotpAttributeIfExists(AuthUser authUser) {
        return userSecurityAttributeService.getAttributeByType(authUser, UserSecurityAttributeType.TOTP);
    }

    public boolean verify(AuthUser authUser, TotpCode code) {
        Optional<UserSecurityAttribute> totpAttributeIfExists = getTotpAttributeIfExists(authUser);
        return totpAttributeIfExists
                .map(UserSecurityAttribute::value)
                .map(TotpSecret::valueOf)
                .map(secret -> codeVerifier.verify(secret, code))
                .orElse(false);

    }

    public void deactivateTotp(AuthUser authUser) {
        userSecurityAttributeService.deleteAttributeByType(authUser, UserSecurityAttributeType.TOTP);
    }

    private QRCodeData toQRCodeData(TotpSecret secret, AuthUser authUser) {
        return QRCodeData.builder()
                .type(totpProperties.qrType())
                .algorithm(totpProperties.totpAlgorithm().getFriendlyName())
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
