package com.github.nramc.dev.journey.api.repository.auth;

import com.github.nramc.dev.journey.api.config.TestContainersConfiguration;
import com.github.nramc.dev.journey.api.config.security.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.Set;

import static com.github.nramc.dev.journey.api.config.TestConfig.ADMIN_USER;
import static com.github.nramc.dev.journey.api.config.TestConfig.AUTHENTICATED_USER;
import static com.github.nramc.dev.journey.api.config.TestConfig.GUEST_USER;
import static com.github.nramc.dev.journey.api.config.TestConfig.TEST_USER;
import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
@Import(TestContainersConfiguration.class)
class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void context() {
        assertThat(userRepository).isNotNull();
    }

    @Test
    void findUserByUsername_shouldReturnUser() {
        userRepository.saveAll(List.of(ADMIN_USER, TEST_USER, AUTHENTICATED_USER, GUEST_USER));

        AuthUser user = userRepository.findUserByUsername(ADMIN_USER.getUsername());
        assertThat(user).isNotNull();
    }

    @Test
    void findUserByUsername_whenUserNotExists_shouldReturnUser() {
        userRepository.saveAll(List.of(ADMIN_USER, TEST_USER, AUTHENTICATED_USER, GUEST_USER));

        AuthUser user = userRepository.findUserByUsername("unknown user");
        assertThat(user).isNull();
    }

    @Test
    void deleteByUsername_shouldDeleteUser() {
        userRepository.saveAll(List.of(ADMIN_USER, TEST_USER, AUTHENTICATED_USER, GUEST_USER));
        assertThat(userRepository.count()).isEqualTo(4);

        userRepository.deleteByUsername(ADMIN_USER.getUsername());

        assertThat(userRepository.count()).isEqualTo(3);
    }

    @Test
    void deleteByUsername_whenUserNotExists_shouldDeleteUserGracefully() {
        userRepository.saveAll(List.of(ADMIN_USER, TEST_USER, AUTHENTICATED_USER, GUEST_USER));
        assertThat(userRepository.count()).isEqualTo(4);

        userRepository.deleteByUsername("unknown user");

        assertThat(userRepository.count()).isEqualTo(4);
    }

    @Test
    void findAdminUsers_shouldReturnAllAvailableAdminUsers() {
        userRepository.saveAll(List.of(ADMIN_USER, TEST_USER, AUTHENTICATED_USER, GUEST_USER));

        List<AuthUser> users = userRepository.findByRolesContainingAndEnabled(Set.of(Role.ADMINISTRATOR), true);
        assertThat(users).hasSize(1);
    }

    @Test
    void findAdminUsers_shouldReturnAllAvailableAdminUsers_shouldExcludeInactiveUsers() {
        userRepository.saveAll(
                List.of(ADMIN_USER, ADMIN_USER.toBuilder().id(null).enabled(false).build())
        );

        List<AuthUser> users = userRepository.findByRolesContainingAndEnabled(Set.of(Role.ADMINISTRATOR), true);
        assertThat(users).hasSize(1);
    }

}