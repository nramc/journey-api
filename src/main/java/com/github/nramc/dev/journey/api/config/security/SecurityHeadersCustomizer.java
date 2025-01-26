package com.github.nramc.dev.journey.api.config.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.security.web.header.writers.XXssProtectionHeaderWriter;
import org.springframework.security.web.util.matcher.AnyRequestMatcher;

@RequiredArgsConstructor
public class SecurityHeadersCustomizer implements Customizer<HeadersConfigurer<HttpSecurity>> {

    @Override
    public void customize(HeadersConfigurer<HttpSecurity> headersConfigurer) {
        headersConfigurer.permissionsPolicyHeader(permissions -> permissions.policy("accelerometer=(),ambient-light-sensor=(),autoplay=(),battery=(),camera=(),display-capture=(),document-domain=(),encrypted-media=(),fullscreen=(self),gamepad=(),geolocation=(),gyroscope=(),layout-animations=(self),legacy-image-formats=(self),magnetometer=(),microphone=(),midi=(),oversized-images=(self),payment=(),picture-in-picture=(),publickey-credentials-stats=(),speaker-selection=(),sync-xhr=(self),unoptimized-images=(self),unsized-media=(self),usb=(),screen-wake-lock=(),web-share=(),xr-spatial-tracking=()"));

        headersConfigurer.referrerPolicy(referrer -> referrer.policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.NO_REFERRER));

        headersConfigurer.xssProtection(xssConfig -> xssConfig.headerValue(XXssProtectionHeaderWriter.HeaderValue.ENABLED_MODE_BLOCK));

        headersConfigurer.httpStrictTransportSecurity(hsts -> hsts.preload(true).includeSubDomains(true).requestMatcher(AnyRequestMatcher.INSTANCE));

        headersConfigurer.contentSecurityPolicy(csp -> csp.policyDirectives("script-src 'self';base-uri 'self';child-src 'none';connect-src 'self';font-src 'self';form-action 'self';frame-ancestors 'none';frame-src 'self';img-src 'self' data:;media-src 'none';object-src 'none';style-src 'self' 'unsafe-inline';worker-src 'none';upgrade-insecure-requests;"));
    }
}
