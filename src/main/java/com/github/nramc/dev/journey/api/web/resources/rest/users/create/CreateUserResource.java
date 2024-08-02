package com.github.nramc.dev.journey.api.web.resources.rest.users.create;

import com.github.nramc.dev.journey.api.config.security.Role;
import com.github.nramc.dev.journey.api.repository.auth.AuthUser;
import com.github.nramc.dev.journey.api.web.resources.Resources;
import com.github.nramc.dev.journey.api.web.resources.rest.doc.RestDocCommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Set;

@RestController
@Slf4j
@RequiredArgsConstructor
public class CreateUserResource {
    private final UserDetailsManager userDetailsManager;
    private final PasswordEncoder passwordEncoder;

    @Operation(summary = "Create new application user with corresponding roles")
    @RestDocCommonResponse
    @ApiResponse(responseCode = "201", description = "User created successfully")
    @PostMapping(value = Resources.NEW_USER, consumes = MediaType.APPLICATION_JSON_VALUE)
    @Tag(name = "Administrator Features")
    public ResponseEntity<Void> create(@RequestBody @Valid CreateUserRequest userRequest) {
        AuthUser user = toModel(userRequest);
        userDetailsManager.createUser(user);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "Sign up new account")
    @RestDocCommonResponse
    @ApiResponse(responseCode = "201", description = "Registration completed")
    @PostMapping(value = Resources.SIGNUP, consumes = MediaType.APPLICATION_JSON_VALUE)
    @Tag(name = "Registration")
    public ResponseEntity<Void> signup(@RequestBody @Valid SignupRequest signupRequest) {
        AuthUser user = toModel(signupRequest);
        userDetailsManager.createUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    private AuthUser toModel(SignupRequest signupRequest) {
        return AuthUser.builder()
                .username(signupRequest.username())
                .password(passwordEncoder.encode(signupRequest.password()))
                .name(signupRequest.name())
                .roles(Set.of(Role.AUTHENTICATED_USER))
                .enabled(false)
                .mfaEnabled(false)
                .passwordChangedAt(LocalDateTime.now())
                .createdDate(LocalDateTime.now())
                .build();
    }

    private AuthUser toModel(CreateUserRequest userRequest) {
        return AuthUser.builder()
                .enabled(true)
                .createdDate(LocalDateTime.now())

                .name(userRequest.name())
                .username(userRequest.username())
                .password(passwordEncoder.encode(userRequest.password()))
                .roles(userRequest.roles())
                .build();
    }


}
