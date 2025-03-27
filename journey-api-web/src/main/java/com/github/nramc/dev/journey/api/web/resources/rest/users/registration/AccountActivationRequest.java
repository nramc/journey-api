package com.github.nramc.dev.journey.api.web.resources.rest.users.registration;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import org.hibernate.validator.constraints.UUID;

@Builder(toBuilder = true)
public record AccountActivationRequest(
        @NotBlank @Email String username,
        @NotBlank @UUID String emailToken) {

    @Override
    public String toString() {
        return "AccountActivationRequest{username='***', emailToken='***'}";
    }
}
