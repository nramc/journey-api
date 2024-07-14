package com.github.nramc.dev.journey.api.web.resources.rest.users.security.totp;

import com.github.nramc.dev.journey.api.models.core.SecurityAttributeType;
import com.github.nramc.dev.journey.api.repository.auth.AuthUser;
import com.github.nramc.dev.journey.api.repository.auth.UserSecurityAttributeEntity;
import com.github.nramc.dev.journey.api.repository.auth.UserSecurityAttributesRepository;
import com.github.nramc.dev.journey.api.security.totp.QRCodeGenerator;
import com.github.nramc.dev.journey.api.security.totp.TotpCodeVerifier;
import com.github.nramc.dev.journey.api.security.totp.TotpSecretGenerator;
import com.github.nramc.dev.journey.api.security.totp.config.TotpProperties;
import com.github.nramc.dev.journey.api.security.totp.model.QRCodeData;
import com.github.nramc.dev.journey.api.security.totp.model.TotpCode;
import com.github.nramc.dev.journey.api.security.totp.model.TotpSecret;
import com.github.nramc.dev.journey.api.web.dto.user.security.UserSecurityAttribute;
import com.github.nramc.dev.journey.api.web.dto.user.security.UserSecurityAttributeConverter;
import com.github.nramc.dev.journey.api.web.exceptions.BusinessException;
import com.github.nramc.dev.journey.api.web.resources.rest.users.security.utils.SecurityAttributesUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
public class TotpService {
    private final TotpProperties totpProperties;
    private final TotpSecretGenerator secretGenerator;
    private final QRCodeGenerator qrCodeGenerator;
    private final TotpCodeVerifier codeVerifier;
    private final UserSecurityAttributesRepository attributesRepository;

    public QRImageDetails newQRCodeData(AuthUser authUser) {
        TotpSecret secret = secretGenerator.generate();

        QRCodeData qrCodeData = toQRCodeData(secret, authUser);
        byte[] qrImageData = qrCodeGenerator.generate(qrCodeData);

        return QRImageDetails.builder()
                .secretKey(secret.secret())
                .data(qrImageData)
                .build();
    }

    public void activateTotp(AuthUser authUser, TotpCode code, TotpSecret secret) {
        boolean isCodeValid = codeVerifier.verify(secret, code);
        if (isCodeValid) {
            UserSecurityAttributeEntity totpEntity = SecurityAttributesUtils.newTotpAttribute(authUser).toBuilder()
                    .verified(true)
                    .value(secret.secret())
                    .build();
            attributesRepository.save(totpEntity);
        } else {
            throw new BusinessException("Code not valid", "totp.code.invalid");
        }
    }

    public Optional<UserSecurityAttribute> getTotpAttributeIfExists(AuthUser authUser) {
        List<UserSecurityAttributeEntity> attributes = attributesRepository.findAllByUserIdAndType(
                authUser.getId().toHexString(), SecurityAttributeType.TOTP);
        return Optional.ofNullable(attributes)
                .filter(CollectionUtils::isNotEmpty)
                .map(List::getFirst)
                .map(UserSecurityAttributeConverter::toModel);
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
