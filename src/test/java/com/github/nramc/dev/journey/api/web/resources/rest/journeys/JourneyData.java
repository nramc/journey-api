package com.github.nramc.dev.journey.api.web.resources.rest.journeys;

import com.github.nramc.commons.geojson.domain.Feature;
import com.github.nramc.commons.geojson.domain.FeatureCollection;
import com.github.nramc.commons.geojson.domain.Point;
import com.github.nramc.commons.geojson.domain.Position;
import com.github.nramc.dev.journey.api.config.security.WithMockAuthenticatedUser;
import com.github.nramc.dev.journey.api.core.domain.AppUser;
import com.github.nramc.dev.journey.api.core.journey.security.Visibility;
import com.github.nramc.dev.journey.api.repository.journey.JourneyEntity;
import com.github.nramc.dev.journey.api.repository.journey.JourneyExtendedEntity;
import com.github.nramc.dev.journey.api.repository.journey.JourneyGeoDetailsEntity;
import com.github.nramc.dev.journey.api.repository.journey.JourneyImageDetailEntity;
import com.github.nramc.dev.journey.api.repository.journey.JourneyImagesDetailsEntity;
import com.github.nramc.dev.journey.api.repository.journey.JourneyVideoDetailEntity;
import com.github.nramc.dev.journey.api.repository.journey.JourneyVideosDetailsEntity;
import lombok.experimental.UtilityClass;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;


@UtilityClass
public class JourneyData {
    public static final AppUser AUTHENTICATED_USER = AppUser.builder()
            .username(WithMockAuthenticatedUser.USERNAME)
            .password(WithMockAuthenticatedUser.PASSWORD)
            .roles(WithMockAuthenticatedUser.USER_DETAILS.roles())
            .name(WithMockAuthenticatedUser.USER_DETAILS.name())
            .enabled(true)
            .mfaEnabled(false)
            .build();
    public static final String GEO_LOCATION_JSON = """
            {"type": "Point", "type": "Point", "coordinates": [48.183160038296585, 11.53090747669896]}
            """;
    public static final String NEW_JOURNEY_JSON = """
            {
              "name" : "First Flight Experience",
              "description" : "Travelled first time for work deputation to Germany, Munich city",
              "tags" : ["travel", "germany", "munich"],
              "thumbnail" : "https://example.com/thumbnail.png",
              "journeyDate": "2024-03-27"
            }
            """;
    public static final JourneyEntity JOURNEY_ENTITY = JourneyEntity.builder()
            .id("ecc76991-0137-4152-b3b2-efce70a37ed0")
            .name("First Flight Experience")
            .description("Travelled first time for work deputation to Germany, Munich city")
            .tags(List.of("travel", "germany", "munich"))
            .thumbnail("https://example.com/thumbnail.png")
            .createdDate(LocalDate.of(2024, 3, 27))
            .journeyDate(LocalDate.of(2024, 3, 27))
            .visibilities(Set.of(Visibility.MYSELF))
            .createdBy(WithMockAuthenticatedUser.USERNAME)
            .build();

    public static final JourneyEntity JOURNEY_EXTENDED_ENTITY = JourneyEntity.builder()
            .id("ecc76991-0137-4152-b3b2-efce70a37ed0")
            .name("First Flight Experience")
            .description("Travelled first time for work deputation to Germany, Munich city")
            .tags(List.of("travel", "germany", "munich"))
            .thumbnail("https://example.com/thumbnail.png")
            .createdDate(LocalDate.of(2024, 3, 27))
            .journeyDate(LocalDate.of(2024, 3, 27))
            .visibilities(Set.of(Visibility.MYSELF))
            .createdBy(WithMockAuthenticatedUser.USERNAME)
            .isPublished(false)
            .extended(JourneyExtendedEntity.builder()
                    .geoDetails(getGeoDetailsEntity())
                    .imagesDetails(getImagesDetailsEntity())
                    .videosDetails(newVideosDetailsEntity())
                    .build()
            )
            .build();

    public static JourneyGeoDetailsEntity getGeoDetailsEntity() {
        return JourneyGeoDetailsEntity.builder()
                .title("Airport, Munich, Germany")
                .category("default")
                .city("Munich")
                .country("Germany")
                .location(Point.of(Position.of(48.183160038296585, 11.53090747669896)))
                .geoJson(FeatureCollection.of(Feature.of("Feature_001", Point.of(Position.of(48.183160038296585, 11.53090747669896)), Map.of())))
                .build();
    }

    public static JourneyImagesDetailsEntity getImagesDetailsEntity() {
        return JourneyImagesDetailsEntity.builder()
                .images(List.of(
                        newImageDetailEntityWith("image1.jpg", "asset 1", "Image 1 Title")
                                .toBuilder().isFavorite(true).build(),
                        newImageDetailEntityWith("image2.jpg", "asset 2", "Image 2 Title")
                ))
                .build();
    }

    public static JourneyImageDetailEntity newImageDetailEntityWith(String url, String assetId, String title) {
        return JourneyImageDetailEntity.builder()
                .isThumbnail(false)
                .isFavorite(false)
                .title(title)
                .url(url)
                .assetId(assetId)
                .build();
    }

    public static JourneyVideosDetailsEntity newVideosDetailsEntity() {
        return JourneyVideosDetailsEntity.builder()
                .videos(List.of(newVideoDetailEntityWith("VIDEO_ID_1"), newVideoDetailEntityWith("https://example.com/example.mp4")))
                .build();
    }

    public static JourneyVideoDetailEntity newVideoDetailEntityWith(String videoId) {
        return JourneyVideoDetailEntity.builder().videoId(videoId).build();
    }
}
