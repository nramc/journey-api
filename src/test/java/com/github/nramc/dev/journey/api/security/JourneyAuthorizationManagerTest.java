package com.github.nramc.dev.journey.api.security;

import com.github.nramc.commons.geojson.domain.Point;
import com.github.nramc.commons.geojson.domain.Position;
import com.github.nramc.dev.journey.api.repository.journey.JourneyEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static com.github.nramc.dev.journey.api.security.Visibility.ADMINISTRATOR;
import static com.github.nramc.dev.journey.api.security.Visibility.AUTHENTICATED_USER;
import static com.github.nramc.dev.journey.api.security.Visibility.MAINTAINER;
import static com.github.nramc.dev.journey.api.security.Visibility.MYSELF;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class JourneyAuthorizationManagerTest {
    private static final JourneyEntity JOURNEY_ENTITY = JourneyEntity.builder()
            .id("ecc76991-0137-4152-b3b2-efce70a37ed0")
            .name("First Flight Experience")
            .title("One of the most beautiful experience ever in my life")
            .description("Travelled first time for work deputation to Germany, Munich city")
            .category("Travel")
            .city("Munich")
            .country("Germany")
            .tags(List.of("Travel", "Germany", "Munich"))
            .thumbnail("valid image id")
            .location(Point.of(Position.of(48.183160038296585, 11.53090747669896)))
            .createdDate(LocalDate.of(2024, 3, 27))
            .journeyDate(LocalDate.of(2024, 3, 27))
            .createdBy("xyz123")
            .build();
    private static final JourneyEntity ADMIN_VISIBILITY = JOURNEY_ENTITY.toBuilder().visibilities(Set.of(ADMINISTRATOR)).build();
    private static final JourneyEntity MAINTAINER_VISIBILITY = JOURNEY_ENTITY.toBuilder().visibilities(Set.of(MAINTAINER)).build();
    private static final JourneyEntity AUTHENTICATED_USER_VISIBILITY = JOURNEY_ENTITY.toBuilder().visibilities(Set.of(AUTHENTICATED_USER)).build();
    private static final JourneyEntity GUEST_VISIBILITY = JOURNEY_ENTITY.toBuilder().visibilities(Set.of(Visibility.GUEST)).build();
    private static final JourneyEntity MYSELF_VISIBILITY = JOURNEY_ENTITY.toBuilder().visibilities(Set.of(MYSELF)).build();


    private Authentication authentication;

    @BeforeEach
    void setup() {
        authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("username");
        when(authentication.getAuthorities()).thenReturn(Collections.emptySet());
    }

    @Test
    void isAuthorized_whenLoggedInUserGuest_thenShouldHaveAccessOnlyToJourneysHaveGuestVisibility() {
        doReturn(List.of(new SimpleGrantedAuthority(Roles.GUEST.name()))).when(authentication).getAuthorities();

        assertFalse(JourneyAuthorizationManager.isAuthorized(ADMIN_VISIBILITY, authentication));
        assertFalse(JourneyAuthorizationManager.isAuthorized(MAINTAINER_VISIBILITY, authentication));
        assertFalse(JourneyAuthorizationManager.isAuthorized(AUTHENTICATED_USER_VISIBILITY, authentication));
        assertFalse(JourneyAuthorizationManager.isAuthorized(MYSELF_VISIBILITY, authentication));
        assertTrue(JourneyAuthorizationManager.isAuthorized(GUEST_VISIBILITY, authentication));
    }

    @Test
    void isAuthorized_whenLoggedInUserAuthenticatedUser_thenShouldHaveAccessToJourneysHaveAuthenticatedUserVisibility() {
        doReturn(List.of(new SimpleGrantedAuthority(Roles.AUTHENTICATED_USER.name()))).when(authentication).getAuthorities();

        assertFalse(JourneyAuthorizationManager.isAuthorized(ADMIN_VISIBILITY, authentication));
        assertFalse(JourneyAuthorizationManager.isAuthorized(MAINTAINER_VISIBILITY, authentication));
        assertTrue(JourneyAuthorizationManager.isAuthorized(AUTHENTICATED_USER_VISIBILITY, authentication));
        assertFalse(JourneyAuthorizationManager.isAuthorized(MYSELF_VISIBILITY, authentication));
        assertFalse(JourneyAuthorizationManager.isAuthorized(GUEST_VISIBILITY, authentication));
    }

    @Test
    void isAuthorized_whenLoggedInUserAuthenticatedUser_thenShouldHaveAccessToJourneysCreatedByThemself() {
        doReturn(List.of(new SimpleGrantedAuthority(Roles.AUTHENTICATED_USER.name()))).when(authentication).getAuthorities();
        when(authentication.getName()).thenReturn("xyz123");

        assertFalse(JourneyAuthorizationManager.isAuthorized(ADMIN_VISIBILITY, authentication));
        assertFalse(JourneyAuthorizationManager.isAuthorized(MAINTAINER_VISIBILITY, authentication));
        assertTrue(JourneyAuthorizationManager.isAuthorized(AUTHENTICATED_USER_VISIBILITY, authentication));
        assertTrue(JourneyAuthorizationManager.isAuthorized(MYSELF_VISIBILITY, authentication));
        assertFalse(JourneyAuthorizationManager.isAuthorized(GUEST_VISIBILITY, authentication));
    }

    @Test
    void isAuthorized_whenLoggedInUserMaintainer_thenShouldHaveAccessToJourneysHaveMaintainerVisibility() {
        doReturn(List.of(new SimpleGrantedAuthority(Roles.MAINTAINER.name()))).when(authentication).getAuthorities();

        assertFalse(JourneyAuthorizationManager.isAuthorized(ADMIN_VISIBILITY, authentication));
        assertTrue(JourneyAuthorizationManager.isAuthorized(MAINTAINER_VISIBILITY, authentication));
        assertFalse(JourneyAuthorizationManager.isAuthorized(AUTHENTICATED_USER_VISIBILITY, authentication));
        assertFalse(JourneyAuthorizationManager.isAuthorized(MYSELF_VISIBILITY, authentication));
        assertFalse(JourneyAuthorizationManager.isAuthorized(GUEST_VISIBILITY, authentication));
    }

    @Test
    void isAuthorized_whenLoggedInUserAdministrator_thenShouldHaveAccessToJourneysHaveAdministratorVisibility() {
        doReturn(List.of(new SimpleGrantedAuthority(Roles.ADMINISTRATOR.name()))).when(authentication).getAuthorities();

        assertTrue(JourneyAuthorizationManager.isAuthorized(ADMIN_VISIBILITY, authentication));
        assertFalse(JourneyAuthorizationManager.isAuthorized(MAINTAINER_VISIBILITY, authentication));
        assertFalse(JourneyAuthorizationManager.isAuthorized(AUTHENTICATED_USER_VISIBILITY, authentication));
        assertFalse(JourneyAuthorizationManager.isAuthorized(MYSELF_VISIBILITY, authentication));
        assertFalse(JourneyAuthorizationManager.isAuthorized(GUEST_VISIBILITY, authentication));
    }

}