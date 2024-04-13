package com.github.nramc.dev.journey.api.services;

import com.github.nramc.dev.journey.api.repository.auth.AuthUser;
import com.github.nramc.dev.journey.api.repository.auth.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsPasswordService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class AuthUserDetailsService implements UserDetailsManager, UserDetailsPasswordService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findUserByUsername(username);
    }

    @Override
    public UserDetails updatePassword(UserDetails user, String newPassword) {
        throw new UnsupportedOperationException("update password feature not possible");
    }

    @Override
    public void createUser(UserDetails userDetails) {
        AuthUser authUser = AuthUser.builder()
                .username(userDetails.getUsername())
                .password(userDetails.getPassword())
                .roles(AuthorityUtils.authorityListToSet(userDetails.getAuthorities()))
                .enabled(true)
                .createdDate(LocalDateTime.now())
                .build();
        userRepository.save(authUser);
    }

    @Override
    public void updateUser(UserDetails user) {
        throw new UnsupportedOperationException("update user feature not possible");
    }

    @Override
    public void deleteUser(String username) {
        throw new UnsupportedOperationException("delete user feature not possible");
    }

    @Override
    public void changePassword(String oldPassword, String newPassword) {
        throw new UnsupportedOperationException("change password feature not possible");
    }

    @Override
    public boolean userExists(String username) {
        return userRepository.findUserByUsername(username) != null;
    }
}
