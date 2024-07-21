package com.github.nramc.dev.journey.api.web.resources.rest.users.security.attributes.totp;

import com.github.nramc.dev.journey.api.models.core.SecurityAttributeType;
import com.github.nramc.dev.journey.api.repository.auth.UserSecurityAttributeEntity;
import com.github.nramc.dev.journey.api.repository.auth.UserSecurityAttributesRepository;
import com.github.nramc.dev.journey.api.web.resources.rest.users.security.attributes.totp.QRCodeGenerator;
import com.github.nramc.dev.journey.api.web.resources.rest.users.security.attributes.totp.QRImageDetails;
import com.github.nramc.dev.journey.api.web.resources.rest.users.security.attributes.totp.TotpAlgorithm;
import com.github.nramc.dev.journey.api.web.resources.rest.users.security.attributes.totp.TotpCodeVerifier;
import com.github.nramc.dev.journey.api.web.resources.rest.users.security.attributes.totp.TotpSecretGenerator;
import com.github.nramc.dev.journey.api.web.resources.rest.users.security.attributes.totp.TotpService;
import com.github.nramc.dev.journey.api.web.resources.rest.users.security.attributes.totp.config.TotpProperties;
import com.github.nramc.dev.journey.api.web.resources.rest.users.security.attributes.totp.model.QRCodeData;
import com.github.nramc.dev.journey.api.web.resources.rest.users.security.attributes.totp.model.TotpCode;
import com.github.nramc.dev.journey.api.web.resources.rest.users.security.attributes.totp.model.TotpSecret;
import com.github.nramc.dev.journey.api.web.dto.user.security.UserSecurityAttribute;
import com.github.nramc.dev.journey.api.web.exceptions.BusinessException;
import com.github.nramc.dev.journey.api.web.resources.rest.users.security.utils.SecurityAttributesUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static com.github.nramc.dev.journey.api.web.resources.rest.users.UsersData.AUTH_USER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.assertArg;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TotpServiceTest {
    private static final TotpSecret TOTP_SECRET = TotpSecret.valueOf("W3C5B3WKR4AUKFVWYU2WNMYB756OAKWY");
    private static final TotpCode TOTP_CODE = TotpCode.valueOf("123456");
    private static final byte[] QR_IMG_BYTES = "QR_IMG_BYTES".getBytes();
    private static final TotpProperties TOTP_PROPERTIES = TotpProperties.builder()
            .numberOfDigits(6)
            .secretLength(32)
            .totpAlgorithm(TotpAlgorithm.SHA1)
            .timeStepSizeInSeconds(30)
            .maxAllowedTimeStepDiscrepancy(1)
            .build();
    private static final UserSecurityAttributeEntity TOTP_ATTRIBUTE = SecurityAttributesUtils.newTotpAttribute(AUTH_USER)
            .toBuilder()
            .value(TOTP_SECRET.secret())
            .build();
    @Mock
    private TotpSecretGenerator secretGenerator;
    @Mock
    private QRCodeGenerator qrCodeGenerator;
    @Mock
    private TotpCodeVerifier codeVerifier;
    @Mock
    private UserSecurityAttributesRepository attributesRepository;

    private TotpService totpService;

    @BeforeEach
    void setUp() {
        totpService = new TotpService(TOTP_PROPERTIES, secretGenerator, qrCodeGenerator, codeVerifier, attributesRepository);
    }

    @Test
    void newQRCodeData_whenNewQRCodeRequested_shouldGenerateQRCodeWithConfiguredValues() {
        when(secretGenerator.generate()).thenReturn(TOTP_SECRET);
        when(qrCodeGenerator.generateWithLogo(any(QRCodeData.class))).thenReturn(QR_IMG_BYTES);

        QRImageDetails qrImageDetails = totpService.newQRCodeData(AUTH_USER);
        assertThat(qrImageDetails).isNotNull()
                .satisfies(data -> assertThat(data.secretKey()).isNotBlank().isEqualTo(TOTP_SECRET.secret()))
                .satisfies(data -> assertThat(data.data()).isEqualTo(QR_IMG_BYTES));
    }

    @Test
    void activateTotp_whenCodeValid_shouldActivateTotp() {
        when(codeVerifier.verify(TOTP_SECRET, TOTP_CODE)).thenReturn(true);

        totpService.activateTotp(AUTH_USER, TOTP_CODE, TOTP_SECRET);

        verify(attributesRepository).save(assertArg(securityAttribute -> assertThat(securityAttribute).isNotNull()
                .satisfies(attribute -> assertThat(attribute.getType()).isEqualTo(SecurityAttributeType.TOTP))
                .satisfies(attribute -> assertThat(attribute.getValue()).isEqualTo(TOTP_SECRET.secret()))
                .satisfies(attribute -> assertThat(attribute.getUserId()).isEqualTo(AUTH_USER.getId().toHexString()))
                .satisfies(attribute -> assertThat(attribute.getUsername()).isEqualTo(AUTH_USER.getUsername()))
                .satisfies(attribute -> assertThat(attribute.isEnabled()).isTrue())
                .satisfies(attribute -> assertThat(attribute.isVerified()).isTrue())));
    }

    @Test
    void activateTotp_whenCodeInvalid_shouldNotActivateTotp() {
        when(codeVerifier.verify(TOTP_SECRET, TOTP_CODE)).thenReturn(false);

        assertThatExceptionOfType(BusinessException.class).isThrownBy(() -> totpService.activateTotp(AUTH_USER, TOTP_CODE, TOTP_SECRET));

        verifyNoInteractions(attributesRepository);
    }

    @Test
    void getTotpAttributeIfExists_whenAttributeExists_shouldReturnTotpAttribute() {
        when(attributesRepository.findAllByUserIdAndType(AUTH_USER.getId().toHexString(), SecurityAttributeType.TOTP))
                .thenReturn(List.of(TOTP_ATTRIBUTE));

        Optional<UserSecurityAttribute> attributeOptional = totpService.getTotpAttributeIfExists(AUTH_USER);

        assertThat(attributeOptional).isNotEmpty()
                .hasValueSatisfying(attribute -> assertThat(attribute).isNotNull());
    }

    @Test
    void getTotpAttributeIfExists_whenAttributeDoesNotExist_shouldReturnEmptyOptional() {
        Optional<UserSecurityAttribute> attributeOptional = totpService.getTotpAttributeIfExists(AUTH_USER);

        assertThat(attributeOptional).isEmpty();
    }

    @Test
    void verify_whenAttributeExistsAndCodeValid_shouldReturnTrue() {
        when(attributesRepository.findAllByUserIdAndType(AUTH_USER.getId().toHexString(), SecurityAttributeType.TOTP))
                .thenReturn(List.of(TOTP_ATTRIBUTE));
        when(codeVerifier.verify(TOTP_SECRET, TOTP_CODE)).thenReturn(true);

        assertThat(totpService.verify(AUTH_USER, TOTP_CODE)).isTrue();
    }

    @Test
    void verify_whenAttributeExistsAndCodeInvalid_shouldReturnFalse() {
        when(attributesRepository.findAllByUserIdAndType(AUTH_USER.getId().toHexString(), SecurityAttributeType.TOTP))
                .thenReturn(List.of(TOTP_ATTRIBUTE));
        when(codeVerifier.verify(TOTP_SECRET, TOTP_CODE)).thenReturn(false);

        assertThat(totpService.verify(AUTH_USER, TOTP_CODE)).isFalse();
    }

    @Test
    void verify_whenAttributeDoesNotExist_shouldReturnFalse() {
        assertThat(totpService.verify(AUTH_USER, TOTP_CODE)).isFalse();
    }

    @Test
    void deactivateTotp_whenAttributeExists_shouldDeactivateTotp() {
        when(attributesRepository.findAllByUserIdAndType(AUTH_USER.getId().toHexString(), SecurityAttributeType.TOTP))
                .thenReturn(List.of(TOTP_ATTRIBUTE));

        totpService.deactivateTotp(AUTH_USER);

        verify(attributesRepository).deleteAllByUserIdAndType(AUTH_USER.getId().toHexString(), SecurityAttributeType.TOTP);
    }

    @Test
    void deactivateTotp_whenAttributeDoesNotExist_shouldBeGraceful() {
        totpService.deactivateTotp(AUTH_USER);
        verify(attributesRepository, never()).deleteAllByUserIdAndType(anyString(), any(SecurityAttributeType.class));
    }


}