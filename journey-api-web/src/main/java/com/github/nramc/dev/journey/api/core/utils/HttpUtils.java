package com.github.nramc.dev.journey.api.core.utils;


import org.apache.commons.lang3.StringUtils;
import ua_parser.Client;
import ua_parser.Parser;

public final class HttpUtils {
    private static final Parser parser = new Parser();

    private HttpUtils() {
        throw new IllegalStateException("Utility class");
    }


    public static String extractDeviceInfo(String userAgent, String defaultValue) {
        Client client = parser.parse(userAgent);
        if (StringUtils.isBlank(userAgent)) {
            return defaultValue;
        }

        return String.format("%s(%s) %s", client.device.family, client.os.family, client.userAgent.family);
    }
}
