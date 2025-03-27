package com.github.nramc.dev.journey.api.web.resources.rest.users.registration;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record SignupRequest(
        @NotBlank
        @Size(min = 8, max = 50)
        @Email
        String username,
        @NotBlank
        @Size(min = 8, max = 50)
        @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@.#$!%*?&^])[A-Za-z\\d@.#$!%*?&]{8,50}$")
        String password,
        @NotBlank
        @Size(min = 3, max = 50)
        String name) {
}
