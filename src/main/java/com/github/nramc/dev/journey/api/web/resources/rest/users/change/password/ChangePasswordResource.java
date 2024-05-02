package com.github.nramc.dev.journey.api.web.resources.rest.users.change.password;

import com.github.nramc.dev.journey.api.services.AuthUserDetailsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static com.github.nramc.dev.journey.api.web.resources.Resources.CHANGE_MY_PASSWORD;

@RestController
@Slf4j
@RequiredArgsConstructor
public class ChangePasswordResource {
    private final AuthUserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    @PostMapping(value = CHANGE_MY_PASSWORD, consumes = MediaType.APPLICATION_JSON_VALUE)
    public void change(@RequestBody @Valid ChangePasswordRequest changePasswordRequest, Authentication authentication) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(authentication.getName());
        userDetailsService.updatePassword(userDetails, passwordEncoder.encode(changePasswordRequest.newPassword()));
    }
}
