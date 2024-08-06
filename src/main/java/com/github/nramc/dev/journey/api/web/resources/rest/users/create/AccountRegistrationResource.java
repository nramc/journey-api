package com.github.nramc.dev.journey.api.web.resources.rest.users.create;

import com.github.nramc.dev.journey.api.config.security.Role;
import com.github.nramc.dev.journey.api.core.model.AppUser;
import com.github.nramc.dev.journey.api.core.usecase.registration.RegistrationUseCase;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@Slf4j
@RequiredArgsConstructor
public class AccountRegistrationResource {
    private final RegistrationUseCase registrationUseCase;

    @Operation(summary = "Sign up new account")
    @RestDocCommonResponse
    @ApiResponse(responseCode = "201", description = "Registration completed")
    @PostMapping(value = Resources.SIGNUP, consumes = MediaType.APPLICATION_JSON_VALUE)
    @Tag(name = "Registration")
    public ResponseEntity<Void> signup(@RequestBody @Valid SignupRequest signupRequest) {
        registrationUseCase.register(
                AppUser.builder()
                        .username(signupRequest.username())
                        .password(signupRequest.password())
                        .name(signupRequest.name())
                        .roles(Set.of(Role.AUTHENTICATED_USER))
                        .build()
        );
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }


}
