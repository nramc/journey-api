package com.github.nramc.dev.journey.api.core.usecase.codes.totp;

import com.github.nramc.dev.journey.api.core.domain.user.UserSecurityAttribute;
import com.github.nramc.dev.journey.api.core.domain.user.settings.security.TotpSecret;
import com.github.nramc.dev.journey.api.core.exceptions.BusinessException;
import com.github.nramc.dev.journey.api.core.usecase.codes.TotpCode;
import com.github.nramc.dev.journey.api.repository.user.attributes.UserSecurityAttributeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static com.github.nramc.dev.journey.api.core.domain.user.UserSecurityAttributeType.TOTP;
import static com.github.nramc.dev.journey.api.web.resources.rest.users.UsersData.AUTHENTICATED_USER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TotpUseCaseTest {
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
    private static final UserSecurityAttribute TOTP_ATTRIBUTE = UserSecurityAttribute.builder()
            .type(TOTP)
            .value(TOTP_SECRET.secret())
            .enabled(true)
            .verified(true)
            .creationDate(LocalDate.now())
            .build();
    @Mock
    private TotpSecretGenerator secretGenerator;
    @Mock
    private QRCodeGenerator qrCodeGenerator;
    @Mock
    private TotpCodeVerifier codeVerifier;
    @Mock
    private UserSecurityAttributeService userSecurityAttributeService;

    private TotpUseCase totpUseCase;

    @BeforeEach
    void setUp() {
        totpUseCase = new TotpUseCase(TOTP_PROPERTIES, secretGenerator, qrCodeGenerator, codeVerifier, userSecurityAttributeService);
    }

    @Test
    void newQRCodeData_whenNewQRCodeRequested_shouldGenerateQRCodeWithConfiguredValues() {
        when(secretGenerator.generate()).thenReturn(TOTP_SECRET);
        when(qrCodeGenerator.generateWithLogo(any())).thenReturn(QR_IMG_BYTES);

        QRImageDetails qrImageDetails = totpUseCase.newQRCodeData(AUTHENTICATED_USER);
        assertThat(qrImageDetails).isNotNull()
                .satisfies(data -> assertThat(data.secretKey()).isNotBlank().isEqualTo(TOTP_SECRET.secret()))
                .satisfies(data -> assertThat(data.data()).isEqualTo(QR_IMG_BYTES));
    }

    @Test
    void activateTotp_whenCodeValid_shouldActivateTotp() {
        when(codeVerifier.verify(TOTP_SECRET, TOTP_CODE)).thenReturn(true);

        totpUseCase.activateTotp(AUTHENTICATED_USER, TOTP_CODE, TOTP_SECRET);

        verify(userSecurityAttributeService).saveTOTPSecret(AUTHENTICATED_USER, TOTP_SECRET);
    }

    @Test
    void activateTotp_whenCodeInvalid_shouldNotActivateTotp() {
        when(codeVerifier.verify(TOTP_SECRET, TOTP_CODE)).thenReturn(false);

        assertThatExceptionOfType(BusinessException.class).isThrownBy(() -> totpUseCase.activateTotp(AUTHENTICATED_USER, TOTP_CODE, TOTP_SECRET));

        verifyNoInteractions(userSecurityAttributeService);
    }

    @Test
    void getTotpAttributeIfExists_whenAttributeExists_shouldReturnTotpAttribute() {
        when(userSecurityAttributeService.getAttributeByType(AUTHENTICATED_USER, TOTP)).thenReturn(Optional.of(TOTP_ATTRIBUTE));

        Optional<UserSecurityAttribute> attributeOptional = totpUseCase.getTotpAttributeIfExists(AUTHENTICATED_USER);

        assertThat(attributeOptional).isNotEmpty().hasValueSatisfying(attribute -> assertThat(attribute).isNotNull());
    }

    @Test
    void getTotpAttributeIfExists_whenAttributeDoesNotExist_shouldReturnEmptyOptional() {
        when(userSecurityAttributeService.getAttributeByType(AUTHENTICATED_USER, TOTP)).thenReturn(Optional.empty());

        Optional<UserSecurityAttribute> attributeOptional = totpUseCase.getTotpAttributeIfExists(AUTHENTICATED_USER);

        assertThat(attributeOptional).isEmpty();
    }

    @Test
    void verify_whenAttributeExistsAndCodeValid_shouldReturnTrue() {
        when(userSecurityAttributeService.getAttributeByType(AUTHENTICATED_USER, TOTP)).thenReturn(Optional.of(TOTP_ATTRIBUTE));

        when(codeVerifier.verify(TOTP_SECRET, TOTP_CODE)).thenReturn(true);

        assertThat(totpUseCase.verify(AUTHENTICATED_USER, TOTP_CODE)).isTrue();
    }

    @Test
    void verify_whenAttributeExistsAndCodeInvalid_shouldReturnFalse() {
        when(userSecurityAttributeService.getAttributeByType(AUTHENTICATED_USER, TOTP)).thenReturn(Optional.of(TOTP_ATTRIBUTE));

        when(codeVerifier.verify(TOTP_SECRET, TOTP_CODE)).thenReturn(false);

        assertThat(totpUseCase.verify(AUTHENTICATED_USER, TOTP_CODE)).isFalse();
    }

    @Test
    void verify_whenAttributeDoesNotExist_shouldReturnFalse() {
        assertThat(totpUseCase.verify(AUTHENTICATED_USER, TOTP_CODE)).isFalse();
    }

    @Test
    void deactivateTotp_shouldDeactivateTotp() {
        totpUseCase.deactivateTotp(AUTHENTICATED_USER);

        verify(userSecurityAttributeService).deleteAttributeByType(AUTHENTICATED_USER, TOTP);
    }


}
