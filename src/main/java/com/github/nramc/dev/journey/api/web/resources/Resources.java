package com.github.nramc.dev.journey.api.web.resources;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Resources {
    public static final String HOME = "/";
    public static final String HEALTH_CHECK = "/actuator/health";

    public static final String GUEST_LOGIN = "/rest/guestLogin";
    public static final String LOGIN = "/rest/login";
    public static final String ALL_REQUESTS = "/**";

    public static final String NEW_JOURNEY = "/rest/journey";
    public static final String FIND_JOURNEY_BY_ID = "/rest/journey/{id}";
    public static final String FIND_JOURNEYS = "/rest/journeys";
    public static final String FIND_PUBLISHED_JOURNEYS = "/rest/journeys/published";
    public static final String UPDATE_JOURNEY = "/rest/journey/{id}";

    public static final String NEW_USER = "/rest/user/new";
    public static final String FIND_USERS = "/rest/users";
    public static final String DELETE_USER_BY_USERNAME = "/rest/user/{username}";

    public static final String FIND_MY_ACCOUNT = "/rest/my-account";
    public static final String DELETE_MY_ACCOUNT = "/rest/my-account";
    public static final String CHANGE_MY_PASSWORD = "/rest/my-account/changePassword";
    public static final String UPDATE_MY_ACCOUNT = "/rest/my-account";

    @UtilityClass
    public static class MediaType {
        public static final String UPDATE_JOURNEY_BASIC_DETAILS = "application/vnd.journey.api.basic.v1+json";
        public static final String UPDATE_JOURNEY_GEO_DETAILS = "application/vnd.journey.api.geo.v1+json";
        public static final String UPDATE_JOURNEY_IMAGES_DETAILS = "application/vnd.journey.api.images.v1+json";
        public static final String UPDATE_JOURNEY_VIDEOS_DETAILS = "application/vnd.journey.api.videos.v1+json";
        public static final String PUBLISH_JOURNEY_DETAILS = "application/vnd.journey.api.publish.v1+json";
        public static final String JOURNEYS_GEO_JSON = "application/geo+json";
    }
}
