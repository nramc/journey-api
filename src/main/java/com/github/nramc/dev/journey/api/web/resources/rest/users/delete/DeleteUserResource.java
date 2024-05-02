package com.github.nramc.dev.journey.api.web.resources.rest.users.delete;

import com.github.nramc.dev.journey.api.repository.auth.AuthUser;
import com.github.nramc.dev.journey.api.services.AuthUserDetailsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import static com.github.nramc.dev.journey.api.web.resources.Resources.DELETE_MY_ACCOUNT;
import static com.github.nramc.dev.journey.api.web.resources.Resources.DELETE_USER_BY_USERNAME;

@RestController
@Slf4j
@RequiredArgsConstructor
public class DeleteUserResource {
    private final AuthUserDetailsService userDetailsService;

    @DeleteMapping(DELETE_USER_BY_USERNAME)
    void deleteByUsername(@PathVariable String username) {
        userDetailsService.deleteUser(username);
    }

    @DeleteMapping(DELETE_MY_ACCOUNT)
    void deleteMyAccount(Authentication authentication) {
        AuthUser userDetails = (AuthUser) userDetailsService.loadUserByUsername(authentication.getName());
        userDetails.setEnabled(false);
        userDetailsService.updateUser(userDetails);
    }
}
