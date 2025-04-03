package com.github.nramc.dev.journey.api.web.resources;

public final class Resources {
    public static final String HOME = "/";
    public static final String HEALTH_CHECK = "/actuator/health";
    public static final String REST_DOC = "/doc/**";

    public static final String API_VERSION = "/rest/version";
    public static final String GUEST_LOGIN = "/rest/guestLogin";
    public static final String LOGIN = "/rest/login";
    public static final String LOGIN_MFA = "/rest/mfa";

    public static final String NEW_JOURNEY = "/rest/journey";
    public static final String FIND_JOURNEY_BY_ID = "/rest/journey/{id}";
    public static final String FIND_JOURNEYS = "/rest/journeys";
    public static final String FIND_UPCOMING_ANNIVERSARY = "/rest/journeys/upcomingAnniversary";
    public static final String FIND_PUBLISHED_JOURNEYS = "/rest/journeys/published";
    public static final String GET_STATISTICS = "/rest/journeys/statistics";

    public static final String GET_TIMELINE_DATA = "/rest/timeline";

    public static final String UPDATE_JOURNEY = "/rest/journey/{id}";
    public static final String DELETE_JOURNEY = "/rest/journey/{id}";

    public static final String SIGNUP = "/rest/signup";
    public static final String ACTIVATE_ACCOUNT = "/rest/activate";
    public static final String FIND_USERS = "/rest/users";
    public static final String DELETE_USER_BY_USERNAME = "/rest/user/{username}";

    public static final String FIND_MY_ACCOUNT = "/rest/my-account";
    public static final String DELETE_MY_ACCOUNT = "/rest/my-account";
    public static final String CHANGE_MY_PASSWORD = "/rest/my-account/changePassword";
    public static final String UPDATE_MY_ACCOUNT = "/rest/my-account";
    public static final String MY_SECURITY_MFA = "/rest/my-account/securityAttribute/mfa";
    public static final String MY_SECURITY_ATTRIBUTE_EMAIL = "/rest/my-account/securityAttribute/emailAddress";
    public static final String MY_SECURITY_ATTRIBUTE_TOTP = "/rest/my-account/securityAttribute/totp";
    public static final String MY_SECURITY_ATTRIBUTE_TOTP_STATUS = "/rest/my-account/securityAttribute/totp/status";
    public static final String MY_SECURITY_ATTRIBUTE_TOTP_VERIFY = "/rest/my-account/securityAttribute/totp/verify";

    public static final String SEND_EMAIL_CODE = "/rest/sendEmailCode";
    public static final String VERIFY_EMAIL_CODE = "/rest/verifyEmailCode";

    public static final String FETCH_ALL_CATEGORIES = "/rest/categories";

    public static final class MediaType {
        public static final String UPDATE_JOURNEY_BASIC_DETAILS = "application/vnd.journey.api.basic.v1+json";
        public static final String UPDATE_JOURNEY_GEO_DETAILS = "application/vnd.journey.api.geo.v1+json";
        public static final String UPDATE_JOURNEY_IMAGES_DETAILS = "application/vnd.journey.api.images.v1+json";
        public static final String UPDATE_JOURNEY_VIDEOS_DETAILS = "application/vnd.journey.api.videos.v1+json";
        public static final String PUBLISH_JOURNEY_DETAILS = "application/vnd.journey.api.publish.v1+json";
        public static final String JOURNEYS_GEO_JSON = "application/geo+json";

        private MediaType() {
            throw new IllegalStateException("Utility class");
        }
    }

    private Resources() {
        throw new IllegalStateException("Utility class");
    }
}
