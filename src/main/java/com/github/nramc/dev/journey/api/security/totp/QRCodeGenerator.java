package com.github.nramc.dev.journey.api.security.totp;

import com.github.nramc.dev.journey.api.security.totp.model.QRCodeData;
import com.github.nramc.dev.journey.api.web.exceptions.BusinessException;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import lombok.RequiredArgsConstructor;

import java.io.ByteArrayOutputStream;

@RequiredArgsConstructor
public class QRCodeGenerator {

    public byte[] generate(QRCodeData qrCodeData) {
        try {
            QRCodeWriter writer = new QRCodeWriter();

            BitMatrix bitMatrix = writer.encode(qrCodeData.getUri(), BarcodeFormat.QR_CODE,
                    qrCodeData.width(), qrCodeData.height());
            ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);

            return pngOutputStream.toByteArray();
        } catch (Exception ex) {
            throw new BusinessException("Failed to generate QR code.", ex.getMessage());
        }

    }

}
