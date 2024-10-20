package com.github.nramc.dev.journey.api.web.resources.rest.journeys;

import com.github.nramc.commons.geojson.domain.Point;
import com.github.nramc.commons.geojson.domain.Position;
import com.github.nramc.dev.journey.api.core.domain.AppUser;
import com.github.nramc.dev.journey.api.core.domain.user.Role;
import com.github.nramc.dev.journey.api.core.journey.security.Visibility;
import com.github.nramc.dev.journey.api.repository.journey.JourneyEntity;
import com.github.nramc.dev.journey.api.repository.journey.JourneyExtendedEntity;
import com.github.nramc.dev.journey.api.repository.journey.JourneyGeoDetailsEntity;
import com.github.nramc.dev.journey.api.repository.journey.JourneyImageDetailEntity;
import com.github.nramc.dev.journey.api.repository.journey.JourneyImagesDetailsEntity;
import lombok.experimental.UtilityClass;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@UtilityClass
public class JourneyData {
    public static final AppUser AUTHENTICATED_USER = AppUser.builder()
            .username("test-user@example.com")
            .password("test-password")
            .roles(Set.of(Role.AUTHENTICATED_USER))
            .name("Authenticated User")
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
            .createdBy("test-user")
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
            .createdBy("test-user")
            .extended(JourneyExtendedEntity.builder()
                    .geoDetails(JourneyGeoDetailsEntity.builder()
                            .title("One of the most beautiful experience ever in my life")
                            .category("Home")
                            .city("Munich")
                            .country("Germany")
                            .location(Point.of(Position.of(48.183160038296585, 11.53090747669896)))
                            .build())
                    .imagesDetails(getImagesDetailsEntity())
                    .build()
            )
            .build();

    public static JourneyImagesDetailsEntity getImagesDetailsEntity() {
        return JourneyImagesDetailsEntity.builder()
                .images(List.of(
                        newImageDetailEntityWith("src_1", "asset 1", "title 1")
                                .toBuilder().isFavorite(true).build(),
                        newImageDetailEntityWith("src_2", "asset 2", "title 2")
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
}
