package com.github.nramc.dev.journey.api.account.repository;

import com.github.nramc.dev.journey.api.shared.domain.EmailAddress;
import com.github.nramc.dev.journey.api.shared.domain.user.security.Role;
import com.github.nramc.dev.journey.api.shared.provider.ActiveUserProvider.ActiveUser;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private AuthUserDetailsService userDetailsService;

    private AuthUser user(String username, String name, boolean enabled, Set<Role> roles) {
        return AuthUser.builder()
                .username(username)
                .name(name)
                .password("encoded-password")
                .enabled(enabled)
                .roles(roles)
                .createdDate(LocalDateTime.now().minusDays(1))
                .build();
    }

    @Nested
    class LoadUserByUsername {

        @Test
        void shouldReturnUserFromRepository() {
            var expected = user("john@example.com", "John", true, Set.of(Role.AUTHENTICATED_USER));
            when(userRepository.findUserByUsername("john@example.com")).thenReturn(expected);

            var actual = userDetailsService.loadUserByUsername("john@example.com");

            assertThat(actual).isSameAs(expected);
            verify(userRepository).findUserByUsername("john@example.com");
        }
    }

    @Nested
    class GetGuestUserDetails {

        @Test
        void shouldLoadGuestUserByFixedUsername() {
            var guest = user("GUEST", "Guest", true, Set.of(Role.GUEST_USER));
            when(userRepository.findUserByUsername("GUEST")).thenReturn(guest);

            var actual = userDetailsService.getGuestUserDetails();

            assertThat(actual).isSameAs(guest);
            verify(userRepository).findUserByUsername("GUEST");
        }
    }

    @Nested
    class UpdatePassword {

        @Test
        void shouldUpdatePasswordAndPasswordChangedAtAndPersist() {
            var existing = user("jane@example.com", "Jane", true, Set.of(Role.AUTHENTICATED_USER));
            when(userRepository.findUserByUsername("jane@example.com")).thenReturn(existing);

            var returned = userDetailsService.updatePassword(existing, "new-password");

            assertThat(returned).isSameAs(existing);
            assertThat(existing.getPassword()).isEqualTo("new-password");
            assertThat(existing.getPasswordChangedAt()).isNotNull();

            var savedCaptor = ArgumentCaptor.forClass(AuthUser.class);
            verify(userRepository).save(savedCaptor.capture());
            assertThat(savedCaptor.getValue()).isSameAs(existing);
        }
    }

    @Nested
    class CreateUser {

        @Test
        void shouldDelegateSaveWithAuthUser() {
            var user = user("create@example.com", "Create", true, Set.of(Role.AUTHENTICATED_USER));

            userDetailsService.createUser(user);

            verify(userRepository).save(user);
        }
    }

    @Nested
    class UpdateUser {

        @Test
        void shouldDelegateSaveWithAuthUser() {
            var user = user("update@example.com", "Update", true, Set.of(Role.MAINTAINER));

            userDetailsService.updateUser(user);

            verify(userRepository).save(user);
        }
    }

    @Nested
    class DeleteUser {

        @Test
        void shouldDelegateDeleteByUsername() {
            userDetailsService.deleteUser("delete@example.com");

            verify(userRepository).deleteByUsername("delete@example.com");
        }
    }

    @Nested
    class ChangePassword {

        @Test
        void shouldThrowUnsupportedOperationException() {
            assertThatThrownBy(() -> userDetailsService.changePassword("old", "new"))
                    .isInstanceOf(UnsupportedOperationException.class)
                    .hasMessage("change password feature not possible");
        }
    }

    @Nested
    class UserExists {

        @Test
        void shouldReturnTrueWhenUserFound() {
            when(userRepository.findUserByUsername("found@example.com"))
                    .thenReturn(user("found@example.com", "Found", true, Set.of(Role.AUTHENTICATED_USER)));

            boolean exists = userDetailsService.userExists("found@example.com");

            assertThat(exists).isTrue();
            verify(userRepository).findUserByUsername("found@example.com");
        }

        @Test
        void shouldReturnFalseWhenUserNotFound() {
            when(userRepository.findUserByUsername("missing@example.com")).thenReturn(null);

            boolean exists = userDetailsService.userExists("missing@example.com");

            assertThat(exists).isFalse();
            verify(userRepository).findUserByUsername("missing@example.com");
        }
    }

    @Nested
    class FindAllAdministratorUsers {

        @Test
        void shouldQueryEnabledAdministratorsOnly() {
            var admin = user("admin@example.com", "Admin", true, Set.of(Role.ADMINISTRATOR));
            when(userRepository.findByRolesContainingAndEnabled(Set.of(Role.ADMINISTRATOR), true))
                    .thenReturn(List.of(admin));

            var result = userDetailsService.findAllAdministratorUsers();

            assertThat(result).containsExactly(admin);
            verify(userRepository).findByRolesContainingAndEnabled(Set.of(Role.ADMINISTRATOR), true);
        }
    }

    @Nested
    class GetAdminEmails {

        @Test
        void shouldMapAdminUsersToEmailAddresses() {
            var admin1 = user("admin1@example.com", "Admin One", true, Set.of(Role.ADMINISTRATOR));
            var admin2 = user("admin2@example.com", "Admin Two", true, Set.of(Role.ADMINISTRATOR));
            when(userRepository.findByRolesContainingAndEnabled(Set.of(Role.ADMINISTRATOR), true))
                    .thenReturn(List.of(admin1, admin2));

            var emails = userDetailsService.get();

            assertThat(emails)
                    .extracting(EmailAddress::value)
                    .containsExactly("admin1@example.com", "admin2@example.com");
        }

        @Test
        void shouldReturnEmptyWhenNoAdministratorsFound() {
            when(userRepository.findByRolesContainingAndEnabled(Set.of(Role.ADMINISTRATOR), true))
                    .thenReturn(List.of());

            var emails = userDetailsService.get();

            assertThat(emails).isEmpty();
        }
    }

    @Nested
    class GetActiveUsers {

        @Test
        void shouldMapEnabledUsersToActiveUserProjection() {
            var first = user("first@example.com", "First", true, Set.of(Role.AUTHENTICATED_USER));
            var second = user("second@example.com", "Second", true, Set.of(Role.MAINTAINER));
            when(userRepository.findByEnabled(true)).thenReturn(List.of(first, second));

            var activeUsers = userDetailsService.getActiveUsers();

            assertThat(activeUsers).hasSize(2);
            assertThat(activeUsers)
                    .extracting(ActiveUser::emailAddress)
                    .extracting(EmailAddress::value)
                    .containsExactly("first@example.com", "second@example.com");
            assertThat(activeUsers)
                    .extracting(ActiveUser::displayName)
                    .containsExactly("First", "Second");
            assertThat(activeUsers)
                    .extracting(ActiveUser::roles)
                    .containsExactly(Set.of(Role.AUTHENTICATED_USER), Set.of(Role.MAINTAINER));
        }

        @Test
        void shouldFallbackDisplayNameToUsernameWhenNameBlank() {
            var blankName = user("blank@example.com", "   ", true, Set.of(Role.AUTHENTICATED_USER));
            when(userRepository.findByEnabled(true)).thenReturn(List.of(blankName));

            var activeUsers = userDetailsService.getActiveUsers();

            assertThat(activeUsers)
                    .extracting(ActiveUser::displayName)
                    .containsExactly("blank@example.com");
        }

        @Test
        void shouldFallbackDisplayNameToUsernameWhenNameNull() {
            var nullName = user("null@example.com", null, true, Set.of(Role.AUTHENTICATED_USER));
            when(userRepository.findByEnabled(true)).thenReturn(List.of(nullName));

            var activeUsers = userDetailsService.getActiveUsers();

            assertThat(activeUsers)
                    .extracting(ActiveUser::displayName)
                    .containsExactly("null@example.com");
        }

        @Test
        void shouldReturnEmptyWhenNoActiveUsers() {
            when(userRepository.findByEnabled(true)).thenReturn(List.of());

            var activeUsers = userDetailsService.getActiveUsers();

            assertThat(activeUsers).isEmpty();
            verify(userRepository).findByEnabled(true);
        }
    }

}