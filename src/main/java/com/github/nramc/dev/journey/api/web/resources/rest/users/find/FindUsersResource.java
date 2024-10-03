package com.github.nramc.dev.journey.api.web.resources.rest.users.find;

import com.github.nramc.dev.journey.api.repository.user.AuthUser;
import com.github.nramc.dev.journey.api.repository.user.UserRepository;
import com.github.nramc.dev.journey.api.web.dto.user.User;
import com.github.nramc.dev.journey.api.web.dto.user.UserConverter;
import com.github.nramc.dev.journey.api.web.resources.rest.doc.RestDocCommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.github.nramc.dev.journey.api.web.resources.Resources.FIND_MY_ACCOUNT;
import static com.github.nramc.dev.journey.api.web.resources.Resources.FIND_USERS;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@Slf4j
@RequiredArgsConstructor
public class FindUsersResource {
    private final UserRepository userRepository;

    @Operation(summary = "Get all available user details", tags = {"Administrator Features"})
    @RestDocCommonResponse
    @ApiResponse(responseCode = "200", description = "Available user details")
    @GetMapping(value = FIND_USERS, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<List<User>> find() {
        List<AuthUser> users = userRepository.findAll();
        return ResponseEntity.ok(UserConverter.toUsers(users));
    }

    @Operation(summary = "Get my account details", tags = {"My Account Features"})
    @RestDocCommonResponse
    @ApiResponse(responseCode = "200", description = "User details", content = {
            @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = User.class))
    })
    @GetMapping(value = FIND_MY_ACCOUNT, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<User> findMyAccount(Authentication authentication) {
        AuthUser users = userRepository.findUserByUsername(authentication.getName());
        return ResponseEntity.ok(UserConverter.toUser(users));
    }
}
