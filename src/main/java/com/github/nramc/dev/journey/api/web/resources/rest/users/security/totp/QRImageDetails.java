package com.github.nramc.dev.journey.api.web.resources.rest.users.security.totp;

import lombok.Builder;

import java.util.Arrays;
import java.util.Objects;

@Builder(toBuilder = true)
public record QRImageDetails(
        byte[] data,
        String secretKey
) {
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        QRImageDetails that = (QRImageDetails) o;
        return Objects.deepEquals(data, that.data) && Objects.equals(secretKey, that.secretKey);
    }

    @Override
    public int hashCode() {
        return Objects.hash(Arrays.hashCode(data), secretKey);
    }

    @Override
    public String toString() {
        return "QRImageDetails{" +
                "data='***'" +
                ", secretKey='***" + '\'' +
                '}';
    }
}
