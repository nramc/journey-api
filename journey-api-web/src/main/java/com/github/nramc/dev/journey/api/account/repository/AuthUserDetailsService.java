package com.github.nramc.dev.journey.api.account.repository;

import com.github.nramc.dev.journey.api.shared.domain.EmailAddress;
import com.github.nramc.dev.journey.api.shared.domain.user.security.Role;
import com.github.nramc.dev.journey.api.shared.provider.ActiveUserProvider;
import com.github.nramc.dev.journey.api.shared.provider.AdminEmailProvider;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsPasswordService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Transactional
public class AuthUserDetailsService implements UserDetailsManager, UserDetailsPasswordService, AdminEmailProvider,
        ActiveUserProvider {
    private final UserRepository userRepository;

    @Override
    public @NonNull UserDetails loadUserByUsername(@NonNull String username) throws UsernameNotFoundException {
        AuthUser user = userRepository.findUserByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("user not found");
        }
        return user;
    }

    public AuthUser getGuestUserDetails() {
        return (AuthUser) this.loadUserByUsername("GUEST");
    }

    @Override
    public @NonNull UserDetails updatePassword(UserDetails user, String newPassword) {
        AuthUser authUser = userRepository.findUserByUsername(user.getUsername());
        authUser.setPassword(newPassword);
        authUser.setPasswordChangedAt(LocalDateTime.now());
        userRepository.save(authUser);
        return authUser;
    }

    @Override
    public void createUser(@NonNull UserDetails userDetails) {
        userRepository.save((AuthUser) userDetails);
    }

    @Override
    public void updateUser(@NonNull UserDetails user) {
        userRepository.save((AuthUser) user);
    }

    @Override
    public void deleteUser(@NonNull String username) {
        userRepository.deleteByUsername(username);
    }

    @Override
    public void changePassword(@Nullable String oldPassword, @Nullable String newPassword) {
        throw new UnsupportedOperationException("change password feature not possible");
    }

    @Override
    public boolean userExists(@NonNull String username) {
        return userRepository.findUserByUsername(username) != null;
    }

    public List<AuthUser> findAllAdministratorUsers() {
        return userRepository.findByRolesContainingAndEnabled(Set.of(Role.ADMINISTRATOR), true);
    }

    @Override
    public List<EmailAddress> get() {
        return findAllAdministratorUsers().stream()
                .map(AuthUser::getUsername)
                .map(EmailAddress::valueOf)
                .toList();
    }

    @Override
    public List<ActiveUser> getActiveUsers() {
        return userRepository.findByEnabled(true).stream()
                .map(user -> new ActiveUser(
                        EmailAddress.valueOf(user.getUsername()),
                        StringUtils.defaultIfBlank(user.getName(), user.getUsername()),
                        user.getRoles()
                ))
                .toList();
    }

}
