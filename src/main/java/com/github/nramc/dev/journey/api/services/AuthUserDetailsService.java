package com.github.nramc.dev.journey.api.services;

import com.github.nramc.dev.journey.api.repository.auth.AuthUser;
import com.github.nramc.dev.journey.api.repository.auth.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsPasswordService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;

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
        userRepository.save((AuthUser) userDetails);
    }

    @Override
    public void updateUser(UserDetails user) {
        userRepository.save((AuthUser) user);
    }

    @Override
    public void deleteUser(String username) {
        userRepository.deleteByUsername(username);
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
