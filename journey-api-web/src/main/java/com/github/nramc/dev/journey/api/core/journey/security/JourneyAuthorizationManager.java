package com.github.nramc.dev.journey.api.core.journey.security;

import com.github.nramc.dev.journey.api.repository.journey.JourneyEntity;
import lombok.experimental.UtilityClass;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;

import static com.github.nramc.dev.journey.api.web.resources.rest.auth.utils.AuthUtils.isAdministratorRoleExists;
import static com.github.nramc.dev.journey.api.web.resources.rest.auth.utils.AuthUtils.isAuthenticatedUser;
import static com.github.nramc.dev.journey.api.web.resources.rest.auth.utils.AuthUtils.isGuestUser;
import static com.github.nramc.dev.journey.api.web.resources.rest.auth.utils.AuthUtils.isMaintainerRoleExists;

@UtilityClass
public class JourneyAuthorizationManager {

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
        return StringUtils.equals(authentication.getName(), journeyEntity.getCreatedBy());
    }


}
