package com.github.nramc.dev.journey.api.web.resources.rest.auth.webauthn;

import lombok.Builder;

@Builder(toBuilder = true)
public record HttpClientRequestInfo(String deviceInfo) {
}
