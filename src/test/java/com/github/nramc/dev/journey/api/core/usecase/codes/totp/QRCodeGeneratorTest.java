package com.github.nramc.dev.journey.api.core.usecase.codes.totp;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class QRCodeGeneratorTest {
    private static final TotpProperties TOTP_PROPERTIES = TotpProperties.builder()
            .numberOfDigits(6)
            .secretLength(32)
            .totpAlgorithm(TotpAlgorithm.SHA1)
            .timeStepSizeInSeconds(30)
            .maxAllowedTimeStepDiscrepancy(1)
            .qrType("totp")
            .qrIssuer("Journey")
            .qrWidth(500)
            .qrHeight(500)
            .build();
    private static final QRCodeData QR_DATA = QRCodeData.builder()
            .type(TOTP_PROPERTIES.qrType())
            .label("username")
            .issuer(TOTP_PROPERTIES.qrIssuer())
            .algorithm(TOTP_PROPERTIES.totpAlgorithm().getHmacAlgorithm())
            .digits(TOTP_PROPERTIES.numberOfDigits())
            .period(TOTP_PROPERTIES.timeStepSizeInSeconds())
            .width(TOTP_PROPERTIES.qrWidth())
            .height(TOTP_PROPERTIES.qrHeight())
            .secret("EX47GINFPBK5GNLYLILGD2H6ZLGJNNWB")
            .build();

    @Test
    void something() throws IOException {
        QRCodeGenerator generator = new QRCodeGenerator();
        Path tempFile = Files.write(Files.createTempFile("qr_test", ".png"), generator.generate(QR_DATA));
        assertThat(tempFile).isNotNull();
    }

    @Test
    void qRCodeGenerator() throws IOException {
        QRCodeGenerator generator = new QRCodeGenerator();
        Path tempFile = Files.write(Files.createTempFile("qr_test", ".png"), generator.generateWithLogo(QR_DATA));
        assertThat(tempFile).isNotNull();
    }

}
