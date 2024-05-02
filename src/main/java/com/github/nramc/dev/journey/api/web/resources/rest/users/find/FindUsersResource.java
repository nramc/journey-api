package com.github.nramc.dev.journey.api.web.resources.rest.users.find;

import com.github.nramc.dev.journey.api.repository.auth.AuthUser;
import com.github.nramc.dev.journey.api.repository.auth.UserRepository;
import com.github.nramc.dev.journey.api.web.dto.user.User;
import com.github.nramc.dev.journey.api.web.dto.user.UserConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.github.nramc.dev.journey.api.web.resources.Resources.FIND_MY_ACCOUNT;
import static com.github.nramc.dev.journey.api.web.resources.Resources.FIND_USERS;

@RestController
@Slf4j
@RequiredArgsConstructor
public class FindUsersResource {
    private final UserRepository userRepository;

    @GetMapping(value = FIND_USERS, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<User>> find() {
        List<AuthUser> users = userRepository.findAll();
        return ResponseEntity.ok(UserConverter.toUsers(users));
    }

    @GetMapping(value = FIND_MY_ACCOUNT, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<User> findMyAccount(Authentication authentication) {
        AuthUser users = userRepository.findUserByUsername(authentication.getName());
        return ResponseEntity.ok(UserConverter.toUser(users));
    }
}
