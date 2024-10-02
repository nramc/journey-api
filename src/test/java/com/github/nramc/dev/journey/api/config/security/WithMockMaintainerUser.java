package com.github.nramc.dev.journey.api.config.security;


import org.springframework.security.test.context.support.WithMockUser;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static com.github.nramc.dev.journey.api.core.user.security.Role.Constants.AUTHENTICATED_USER;
import static com.github.nramc.dev.journey.api.core.user.security.Role.Constants.MAINTAINER;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@WithMockUser(username = "test-maintainer", password = "test-password", authorities = {AUTHENTICATED_USER, MAINTAINER})
public @interface WithMockMaintainerUser {
}
