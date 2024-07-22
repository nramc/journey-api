package com.github.nramc.dev.journey.api.web.resources.rest.users.security.attributes.email;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UpdateEmailAddressRequest(@NotBlank @Email String emailAddress) {

}
