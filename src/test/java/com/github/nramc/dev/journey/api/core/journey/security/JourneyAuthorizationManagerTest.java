package com.github.nramc.dev.journey.api.core.journey.security;

import com.github.nramc.dev.journey.api.config.security.WithMockAdministratorUser;
import com.github.nramc.dev.journey.api.config.security.WithMockAuthenticatedUser;
import com.github.nramc.dev.journey.api.config.security.WithMockGuestUser;
import com.github.nramc.dev.journey.api.config.security.WithMockMaintainerUser;
import com.github.nramc.dev.journey.api.core.domain.user.Role;
import com.github.nramc.dev.journey.api.repository.journey.JourneyEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;

import java.util.Set;

import static com.github.nramc.dev.journey.api.core.journey.security.Visibility.ADMINISTRATOR;
import static com.github.nramc.dev.journey.api.core.journey.security.Visibility.AUTHENTICATED_USER;
import static com.github.nramc.dev.journey.api.core.journey.security.Visibility.MAINTAINER;
import static com.github.nramc.dev.journey.api.core.journey.security.Visibility.MYSELF;
import static com.github.nramc.dev.journey.api.web.resources.rest.journeys.JourneyData.JOURNEY_ENTITY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.security.core.authority.AuthorityUtils.createAuthorityList;

class JourneyAuthorizationManagerTest {
    private static final JourneyEntity JOURNEY_WITH_ADMIN_VISIBILITY = JOURNEY_ENTITY.toBuilder().visibilities(Set.of(ADMINISTRATOR)).build();
    private static final JourneyEntity JOURNEY_WITH_MAINTAINER_VISIBILITY = JOURNEY_ENTITY.toBuilder().visibilities(Set.of(MAINTAINER)).build();
    private static final JourneyEntity JOURNEY_WITH_AUTHENTICATED_USER_VISIBILITY = JOURNEY_ENTITY.toBuilder().visibilities(Set.of(AUTHENTICATED_USER)).build();
    private static final JourneyEntity JOURNEY_WITH_GUEST_VISIBILITY = JOURNEY_ENTITY.toBuilder().visibilities(Set.of(Visibility.GUEST)).build();
    private static final JourneyEntity JOURNEY_WITH_MYSELF_VISIBILITY = JOURNEY_ENTITY.toBuilder().visibilities(Set.of(MYSELF)).build();


    private Authentication authentication;

    @BeforeEach
    void setup() {
        authentication = mock(Authentication.class);
    }

    @Test
    void isAuthorized_whenLoggedInUserGuest_thenShouldHaveAccessOnlyToJourneysHaveGuestVisibility() {
        when(authentication.getName()).thenReturn(WithMockGuestUser.USERNAME);
        doReturn(createAuthorityList(Role.GUEST_USER.name())).when(authentication).getAuthorities();

        assertThat(JourneyAuthorizationManager.isAuthorized(JOURNEY_WITH_ADMIN_VISIBILITY, authentication)).isFalse();
        assertThat(JourneyAuthorizationManager.isAuthorized(JOURNEY_WITH_MAINTAINER_VISIBILITY, authentication)).isFalse();
        assertThat(JourneyAuthorizationManager.isAuthorized(JOURNEY_WITH_AUTHENTICATED_USER_VISIBILITY, authentication)).isFalse();
        assertThat(JourneyAuthorizationManager.isAuthorized(JOURNEY_WITH_MYSELF_VISIBILITY, authentication)).isFalse();
        assertThat(JourneyAuthorizationManager.isAuthorized(JOURNEY_WITH_GUEST_VISIBILITY, authentication)).isTrue();
    }

    @Test
    void isAuthorized_whenLoggedInUserAuthenticatedUser_thenShouldHaveAccessToJourneysHaveAuthenticatedUserVisibility() {
        when(authentication.getName()).thenReturn(WithMockAuthenticatedUser.MFA_USERNAME);
        doReturn(createAuthorityList(Role.AUTHENTICATED_USER.name())).when(authentication).getAuthorities();

        assertThat(JourneyAuthorizationManager.isAuthorized(JOURNEY_WITH_ADMIN_VISIBILITY, authentication)).isFalse();
        assertThat(JourneyAuthorizationManager.isAuthorized(JOURNEY_WITH_MAINTAINER_VISIBILITY, authentication)).isFalse();
        assertThat(JourneyAuthorizationManager.isAuthorized(JOURNEY_WITH_AUTHENTICATED_USER_VISIBILITY, authentication)).isTrue();
        assertThat(JourneyAuthorizationManager.isAuthorized(JOURNEY_WITH_MYSELF_VISIBILITY, authentication)).isFalse();
        assertThat(JourneyAuthorizationManager.isAuthorized(JOURNEY_WITH_GUEST_VISIBILITY, authentication)).isFalse();
    }

    @Test
    void isAuthorized_whenLoggedInUserAuthenticatedUser_thenShouldHaveAccessToJourneysCreatedByThemself() {
        when(authentication.getName()).thenReturn(WithMockAuthenticatedUser.USERNAME);
        doReturn(createAuthorityList(Role.AUTHENTICATED_USER.name())).when(authentication).getAuthorities();

        assertThat(JourneyAuthorizationManager.isAuthorized(JOURNEY_WITH_ADMIN_VISIBILITY, authentication)).isFalse();
        assertThat(JourneyAuthorizationManager.isAuthorized(JOURNEY_WITH_MAINTAINER_VISIBILITY, authentication)).isFalse();
        assertThat(JourneyAuthorizationManager.isAuthorized(JOURNEY_WITH_AUTHENTICATED_USER_VISIBILITY, authentication)).isTrue();
        assertThat(JourneyAuthorizationManager.isAuthorized(JOURNEY_WITH_MYSELF_VISIBILITY, authentication)).isTrue();
        assertThat(JourneyAuthorizationManager.isAuthorized(JOURNEY_WITH_GUEST_VISIBILITY, authentication)).isFalse();
    }

    @Test
    void isAuthorized_whenLoggedInUserMaintainer_thenShouldHaveAccessToJourneysHaveMaintainerVisibility() {
        when(authentication.getName()).thenReturn(WithMockMaintainerUser.USERNAME);
        doReturn(createAuthorityList(Role.MAINTAINER.name(), Role.AUTHENTICATED_USER.name())).when(authentication).getAuthorities();

        assertThat(JourneyAuthorizationManager.isAuthorized(JOURNEY_WITH_ADMIN_VISIBILITY, authentication)).isFalse();
        assertThat(JourneyAuthorizationManager.isAuthorized(JOURNEY_WITH_MAINTAINER_VISIBILITY, authentication)).isTrue();
        assertThat(JourneyAuthorizationManager.isAuthorized(JOURNEY_WITH_AUTHENTICATED_USER_VISIBILITY, authentication)).isTrue();
        assertThat(JourneyAuthorizationManager.isAuthorized(JOURNEY_WITH_MYSELF_VISIBILITY, authentication)).isFalse();
        assertThat(JourneyAuthorizationManager.isAuthorized(JOURNEY_WITH_GUEST_VISIBILITY, authentication)).isFalse();
    }

    @Test
    void isAuthorized_whenLoggedInUserAdministrator_thenShouldHaveAccessToJourneysHaveAdministratorVisibility() {
        when(authentication.getName()).thenReturn(WithMockAdministratorUser.USERNAME);
        doReturn(createAuthorityList(Role.ADMINISTRATOR.name(), Role.AUTHENTICATED_USER.name())).when(authentication).getAuthorities();

        assertThat(JourneyAuthorizationManager.isAuthorized(JOURNEY_WITH_ADMIN_VISIBILITY, authentication)).isTrue();
        assertThat(JourneyAuthorizationManager.isAuthorized(JOURNEY_WITH_MAINTAINER_VISIBILITY, authentication)).isFalse();
        assertThat(JourneyAuthorizationManager.isAuthorized(JOURNEY_WITH_AUTHENTICATED_USER_VISIBILITY, authentication)).isTrue();
        assertThat(JourneyAuthorizationManager.isAuthorized(JOURNEY_WITH_MYSELF_VISIBILITY, authentication)).isFalse();
        assertThat(JourneyAuthorizationManager.isAuthorized(JOURNEY_WITH_GUEST_VISIBILITY, authentication)).isFalse();
    }

}
