package com.github.nramc.dev.journey.api.core.converters;

import com.github.nramc.dev.journey.api.core.user.converters.AppUserConvertor;
import com.github.nramc.dev.journey.api.core.user.security.Role;
import com.github.nramc.dev.journey.api.core.domain.AppUser;
import com.github.nramc.dev.journey.api.repository.auth.AuthUser;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class AppUserConvertorTest {
    private static final AppUser APP_USER = AppUser.builder()
            .name("Onboarding User")
            .username("daran_avilesui@permission.rz")
            .password("omuaI7QEskydXsXEaLqJ!")
            .roles(Set.of(Role.AUTHENTICATED_USER, Role.MAINTAINER))
            .mfaEnabled(true)
            .enabled(true)
            .createdDate(LocalDateTime.now())
            .passwordChangedAt(LocalDateTime.now())
            .build();
    private static final AuthUser USER_ENTITY = AuthUser.builder()
            .id(ObjectId.get())
            .name("Alexzandra Caudell")
            .username("rosaelena_bentonef@root.mpp")
            .password("xt1mTFXWWwiC!")
            .roles(Set.of(Role.AUTHENTICATED_USER, Role.MAINTAINER))
            .mfaEnabled(true)
            .enabled(true)
            .createdDate(LocalDateTime.now())
            .passwordChangedAt(LocalDateTime.now())
            .build();

    @Test
    void toEntity() {
        AuthUser userEntity = AppUserConvertor.toEntity(APP_USER);
        assertThat(userEntity).isNotNull()
                .satisfies(entity -> assertThat(entity.getName()).isEqualTo(APP_USER.name()))
                .satisfies(entity -> assertThat(entity.getUsername()).isEqualTo(APP_USER.username()))
                .satisfies(entity -> assertThat(entity.getPassword()).isEqualTo(APP_USER.password()))
                .satisfies(entity -> assertThat(entity.getRoles()).isSameAs(APP_USER.roles()))
                .satisfies(entity -> assertThat(entity.isEnabled()).isEqualTo(APP_USER.enabled()))
                .satisfies(entity -> assertThat(entity.isMfaEnabled()).isEqualTo(APP_USER.mfaEnabled()))
                .satisfies(entity -> assertThat(entity.getCreatedDate()).isEqualTo(APP_USER.createdDate()))
                .satisfies(entity -> assertThat(entity.getPasswordChangedAt()).isEqualTo(APP_USER.passwordChangedAt()));
    }

    @Test
    void toDomain() {
        AppUser appuser = AppUserConvertor.toDomain(USER_ENTITY);
        assertThat(appuser).isNotNull()
                .satisfies(user -> assertThat(user.name()).isEqualTo(USER_ENTITY.getName()))
                .satisfies(user -> assertThat(user.username()).isEqualTo(USER_ENTITY.getUsername()))
                .satisfies(user -> assertThat(user.password()).isNullOrEmpty())
                .satisfies(user -> assertThat(user.roles()).isEqualTo(USER_ENTITY.getRoles()))
                .satisfies(user -> assertThat(user.enabled()).isEqualTo(USER_ENTITY.isEnabled()))
                .satisfies(user -> assertThat(user.mfaEnabled()).isEqualTo(USER_ENTITY.isMfaEnabled()))
                .satisfies(user -> assertThat(user.createdDate()).isEqualTo(USER_ENTITY.getCreatedDate()))
                .satisfies(user -> assertThat(user.passwordChangedAt()).isEqualTo(USER_ENTITY.getPasswordChangedAt()));
    }

}
