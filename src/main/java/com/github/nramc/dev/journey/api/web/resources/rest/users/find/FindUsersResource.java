package com.github.nramc.dev.journey.api.web.resources.rest.users.find;

import com.github.nramc.dev.journey.api.repository.auth.AuthUser;
import com.github.nramc.dev.journey.api.repository.auth.UserRepository;
import com.github.nramc.dev.journey.api.web.dto.user.User;
import com.github.nramc.dev.journey.api.web.dto.user.UserConverter;
import com.github.nramc.dev.journey.api.web.resources.Resources;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@CrossOrigin(value = "*")
public class FindUsersResource {
    private final UserRepository userRepository;

    @GetMapping(value = Resources.FIND_USERS, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<User>> find() {
        List<AuthUser> users = userRepository.findAll();
        return ResponseEntity.ok(UserConverter.toUsers(users));
    }
}
