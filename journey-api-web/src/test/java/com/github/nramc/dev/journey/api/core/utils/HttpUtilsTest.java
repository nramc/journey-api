package com.github.nramc.dev.journey.api.core.utils;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class HttpUtilsTest {

    @Test
    void extractDeviceInfo() {
        String userAgent = "'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36'";
        String expected = "Mac(Mac OS X) Chrome";
        String deviceInfo = HttpUtils.extractDeviceInfo(userAgent, "Unknown Device");
        assertThat(deviceInfo).isEqualTo(expected);
    }

    @Test
    void extractDeviceInfoWithEmptyUserAgent() {
        String userAgent = "";
        String expected = "Unknown Device";
        String deviceInfo = HttpUtils.extractDeviceInfo(userAgent, expected);
        assertThat(deviceInfo).isEqualTo(expected);
    }

}
