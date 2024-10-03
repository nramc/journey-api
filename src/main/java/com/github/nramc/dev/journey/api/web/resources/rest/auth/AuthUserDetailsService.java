package com.github.nramc.dev.journey.api.web.resources.rest.auth;

import com.github.nramc.dev.journey.api.core.domain.user.Role;
import com.github.nramc.dev.journey.api.repository.user.AuthUser;
import com.github.nramc.dev.journey.api.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsPasswordService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class AuthUserDetailsService implements UserDetailsManager, UserDetailsPasswordService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findUserByUsername(username);
    }

    public AuthUser getGuestUserDetails() {
        return userRepository.findUserByUsername("GUEST");
    }

    @Override
    public UserDetails updatePassword(UserDetails user, String newPassword) {
        AuthUser authUser = userRepository.findUserByUsername(user.getUsername());
        authUser.setPassword(newPassword);
        authUser.setPasswordChangedAt(LocalDateTime.now());
        userRepository.save(authUser);
        return authUser;
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

    public List<AuthUser> findAllAdministratorUsers() {
        return userRepository.findByRolesContainingAndEnabled(Set.of(Role.ADMINISTRATOR), true);
    }

}
