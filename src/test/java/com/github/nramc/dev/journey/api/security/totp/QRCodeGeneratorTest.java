package com.github.nramc.dev.journey.api.security.totp;

import com.github.nramc.dev.journey.api.security.totp.config.TotpProperties;
import com.github.nramc.dev.journey.api.security.totp.model.QRCodeData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

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
    private final QRCodeData QR_DATA = QRCodeData.builder()
            .type(TOTP_PROPERTIES.qrType())
            .label("username")
            .issuer(TOTP_PROPERTIES.qrIssuer())
            .algorithm(TOTP_PROPERTIES.totpAlgorithm().getHmacAlgorithm())
            .digits(TOTP_PROPERTIES.numberOfDigits())
            .period((int) TOTP_PROPERTIES.timeStepSizeInSeconds())
            .width(TOTP_PROPERTIES.qrWidth())
            .height(TOTP_PROPERTIES.qrHeight())
            .secret("EX47GINFPBK5GNLYLILGD2H6ZLGJNNWB")
            .build();

    @Test
    void testSomething() throws IOException {
        QRCodeGenerator generator = new QRCodeGenerator();
        Path tempFile = Files.write(Files.createTempFile("qr_test", ".png"), generator.generate(QR_DATA));
        Assertions.assertNotNull(tempFile);
    }

}