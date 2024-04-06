package com.github.nramc.dev.journey.api.web.resources;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Resources {
    public static final String HOME = "";
    public static final String CREATE_JOURNEY = "/rest/journey";
    public static final String FIND_JOURNEY = "/rest/journey/{id}";
    public static final String FIND_JOURNEYS = "/rest/journeys";
    public static final String UPDATE_JOURNEY = "/rest/journey/{id}";

    @UtilityClass
    public static class MediaType {
        public static final String UPDATE_JOURNEY_BASIC_DETAILS = "application/vnd.journey.api.basic.v1+json";
        public static final String UPDATE_JOURNEY_GEO_DETAILS = "application/vnd.journey.api.geo.v1+json";
        public static final String UPDATE_JOURNEY_IMAGES_DETAILS = "application/vnd.journey.api.images.v1+json";
        public static final String UPDATE_JOURNEY_VIDEOS_DETAILS = "application/vnd.journey.api.videos.v1+json";
    }
}
