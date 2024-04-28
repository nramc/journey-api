package com.github.nramc.dev.journey.api.web.resources.rest.users.update;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateUserRequest(
        @NotBlank @Size(min = 3, max = 50) String name,
        @NotBlank @Email String emailAddress) {
}
