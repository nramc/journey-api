package com.github.nramc.dev.journey.api.web.resources.rest.users.create;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateUserRequest(
        @NotBlank @Size(min = 8, max = 50) String username,
        @NotBlank @Size(min = 8, max = 50) String password,
        @NotBlank @Size(min = 3, max = 50) String name,
        @Email String emailAddress
) {
}
