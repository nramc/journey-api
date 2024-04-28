package com.github.nramc.dev.journey.api.web.resources.rest.users.change.password;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangePasswordRequest(
        @NotBlank @Size(min = 8, max = 50) String oldPassword,
        @NotBlank @Size(min = 8, max = 50) String newPassword
) {
}
