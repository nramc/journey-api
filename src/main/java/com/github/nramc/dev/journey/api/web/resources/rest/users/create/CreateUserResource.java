package com.github.nramc.dev.journey.api.web.resources.rest.users.create;

import com.github.nramc.dev.journey.api.repository.auth.AuthUser;
import com.github.nramc.dev.journey.api.services.AuthUserDetailsService;
import com.github.nramc.dev.journey.api.web.resources.Resources;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@Slf4j
@RequiredArgsConstructor
public class CreateUserResource {
    private final AuthUserDetailsService userDetailsService;


    @PostMapping(value = Resources.NEW_USER, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> create(@RequestBody @Valid CreateUserRequest userRequest) {
        AuthUser user = toUserModel(userRequest);
        userDetailsService.createUser(user);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    private AuthUser toUserModel(CreateUserRequest userRequest) {
        return AuthUser.builder()
                .enabled(true)
                .createdDate(LocalDateTime.now())

                .name(userRequest.name())
                .username(userRequest.username())
                .password(userRequest.password())
                .roles(userRequest.roles())
                .emailAddress(userRequest.emailAddress())
                .build();
    }


}
