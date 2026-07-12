package com.github.nramc.dev.journey.api.account.web.auth.recovery;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

record SendPasswordRecoveryRequest(
        @NotBlank @Email String username
) {
}
