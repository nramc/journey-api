package com.github.nramc.dev.journey.api.core.domain;

import com.github.nramc.dev.journey.api.core.domain.user.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.Set;

@Builder(toBuilder = true)
public record AppUser(
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
        String name,
        LocalDateTime createdDate,
        LocalDateTime passwordChangedAt,
        boolean enabled,
        Set<Role> roles,
        boolean mfaEnabled) {

    @Override
    public String toString() {
        return "AppUser{"
                + "username='" + username + '\''
                + ", name='" + name + '\''
                + ", createdDate=" + createdDate
                + ", passwordChangedAt=" + passwordChangedAt
                + ", enabled=" + enabled
                + ", roles=" + roles
                + ", mfaEnabled=" + mfaEnabled
                + '}';
    }
}
