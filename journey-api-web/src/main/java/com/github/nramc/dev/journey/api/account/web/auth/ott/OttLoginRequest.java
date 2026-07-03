package com.github.nramc.dev.journey.api.account.web.auth.ott;

import jakarta.validation.constraints.NotBlank;

record OttLoginRequest(
        @NotBlank String token
) {
}

