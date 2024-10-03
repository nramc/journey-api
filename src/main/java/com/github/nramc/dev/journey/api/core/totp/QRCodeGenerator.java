package com.github.nramc.dev.journey.api.core.totp;

import com.github.nramc.dev.journey.api.core.exceptions.BusinessException;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageConfig;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.util.ResourceUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

@RequiredArgsConstructor
public class QRCodeGenerator {

    public byte[] generate(QRCodeData qrCodeData) {
        try {
            QRCodeWriter writer = new QRCodeWriter();
            BitMatrix bitMatrix = writer.encode(qrCodeData.getUri(), BarcodeFormat.QR_CODE, qrCodeData.width(), qrCodeData.height());
            ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
            return pngOutputStream.toByteArray();
        } catch (Exception ex) {
            throw new BusinessException("Failed to generate QR code.", ex.getMessage());
        }

    }

    public byte[] generateWithLogo(QRCodeData qrCodeData) {
        try {
            // Generate QR image
            QRCodeWriter writer = new QRCodeWriter();
            BitMatrix bitMatrix = writer.encode(qrCodeData.getUri(), BarcodeFormat.QR_CODE, qrCodeData.width(), qrCodeData.height());
            MatrixToImageConfig imageConfig = new MatrixToImageConfig(MatrixToImageConfig.BLACK, MatrixToImageConfig.WHITE);
            BufferedImage qrImage = MatrixToImageWriter.toBufferedImage(bitMatrix, imageConfig);

            // Getting logo image
            File logoFile = ResourceUtils.getFile("classpath:assets/qr-code-logo-60x60.png");
            BufferedImage logoImage = ImageIO.read(logoFile);
            int finalImageHeight = qrImage.getHeight() - logoImage.getHeight();
            int finalImageWidth = qrImage.getWidth() - logoImage.getWidth();

            //Merging both images
            BufferedImage finalImage = new BufferedImage(qrImage.getHeight(), qrImage.getWidth(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D graphics = (Graphics2D) finalImage.getGraphics();
            graphics.drawImage(qrImage, 0, 0, null);
            graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
            graphics.drawImage(logoImage, Math.ceilDiv(finalImageWidth, 2), Math.ceilDiv(finalImageHeight, 2), null);

            // Adding Issuer text
            FontMetrics fontMetrics = graphics.getFontMetrics();
            int bottomTextWidth = fontMetrics.stringWidth(qrCodeData.issuer());
            graphics.setColor(Color.BLUE);
            graphics.setFont(new Font("Serif", Font.BOLD, 18));
            graphics.drawString(qrCodeData.issuer(), (bitMatrix.getWidth() - bottomTextWidth) / 2, bitMatrix.getHeight() - 20);

            // convert to bytes
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ImageIO.write(finalImage, "png", byteArrayOutputStream);
            return byteArrayOutputStream.toByteArray();
        } catch (IOException | WriterException ex) {
            throw new BusinessException("Failed to generate QR code.", ex.getMessage());
        }
    }


}
