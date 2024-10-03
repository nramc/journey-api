package com.github.nramc.dev.journey.api.config.security;


import org.springframework.security.test.context.support.WithMockUser;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static com.github.nramc.dev.journey.api.core.domain.user.Role.Constants.AUTHENTICATED_USER;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@WithMockUser(username = "test.user@example.com", password = "test-password", authorities = {AUTHENTICATED_USER})
public @interface WithMockAuthenticatedUser {
}
