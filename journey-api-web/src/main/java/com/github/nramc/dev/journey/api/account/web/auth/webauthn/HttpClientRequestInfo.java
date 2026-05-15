package com.github.nramc.dev.journey.api.account.web.auth.webauthn;

import lombok.Builder;

@Builder(toBuilder = true)
public record HttpClientRequestInfo(String deviceInfo) {
}
