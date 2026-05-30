package com.github.nramc.dev.journey.api.account.web.users.update;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateUserRequest(
        @NotBlank @Size(min = 3, max = 50) String name) {
}
