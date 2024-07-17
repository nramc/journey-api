package com.github.nramc.dev.journey.api.security.totp.model;

import lombok.Builder;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Builder(toBuilder = true)
public record QRCodeData(
        String type,
        String label,
        String secret,
        String issuer,
        String algorithm,
        int digits,
        long period,
        int width,
        int height) {
    public String getUri() {
        return "otpauth://" +
                uriEncode(type) + "/" +
                uriEncode(label) + "?" +
                "secret=" + uriEncode(secret) +
                "&issuer=" + uriEncode(issuer) +
                "&algorithm=" + uriEncode(algorithm) +
                "&digits=" + digits +
                "&period=" + period +
                "&image=" + uriEncode("https://nramc.github.io/journeys/assets/journey-logo.png");
    }

    private String uriEncode(String text) {
        if (text == null) {
            return "";
        }
        return URLEncoder.encode(text, StandardCharsets.UTF_8)
                .replace("\\+", "%20");
    }

    @Override
    public String toString() {
        return "QRCodeData{" +
                "type='" + type + '\'' +
                ", label='" + label + '\'' +
                ", secret='***" + '\'' +
                ", issuer='" + issuer + '\'' +
                ", algorithm='" + algorithm + '\'' +
                ", digits=" + digits +
                ", period=" + period +
                ", width=" + width +
                ", height=" + height +
                '}';
    }
}
