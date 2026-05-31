package com.github.nramc.dev.journey.api.journey.domain.security;

import com.github.nramc.dev.journey.api.journey.repository.JourneyEntity;
import com.github.nramc.dev.journey.api.shared.domain.user.security.Visibility;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;

import java.util.Objects;

import static com.github.nramc.dev.journey.api.shared.utils.AuthUtils.isAdministratorRoleExists;
import static com.github.nramc.dev.journey.api.shared.utils.AuthUtils.isAuthenticatedUser;
import static com.github.nramc.dev.journey.api.shared.utils.AuthUtils.isGuestUser;
import static com.github.nramc.dev.journey.api.shared.utils.AuthUtils.isMaintainerRoleExists;

public final class JourneyAuthorizationManager {

    public static boolean isAuthorized(JourneyEntity journeyEntity, Authentication authentication) {

        return CollectionUtils.emptyIfNull(journeyEntity.getVisibilities())
                .stream()
                .anyMatch(visibility -> isJourneyVisible(visibility, journeyEntity, authentication));
    }

    private static boolean isJourneyVisible(Visibility visibility, JourneyEntity journeyEntity, Authentication authentication) {
        return switch (visibility) {
            case MYSELF -> isJourneyOwnedByLoggedInUser(journeyEntity, authentication);
            case ADMINISTRATOR -> isAdministratorRoleExists(authentication.getAuthorities());
            case MAINTAINER -> isMaintainerRoleExists(authentication.getAuthorities());
            case AUTHENTICATED_USER -> isAuthenticatedUser(authentication.getAuthorities());
            case GUEST -> isGuestUser(authentication.getAuthorities());
        };
    }

    private static boolean isJourneyOwnedByLoggedInUser(JourneyEntity journeyEntity, Authentication authentication) {
        return Objects.equals(authentication.getName(), journeyEntity.getCreatedBy());
    }

    private JourneyAuthorizationManager() {
        throw new IllegalStateException("Utility class");
    }


}
