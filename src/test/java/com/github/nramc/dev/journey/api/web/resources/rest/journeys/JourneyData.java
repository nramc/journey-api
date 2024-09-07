package com.github.nramc.dev.journey.api.web.resources.rest.journeys;

import com.github.nramc.commons.geojson.domain.Point;
import com.github.nramc.commons.geojson.domain.Position;
import com.github.nramc.dev.journey.api.repository.journey.JourneyEntity;
import com.github.nramc.dev.journey.api.repository.journey.JourneyExtendedEntity;
import com.github.nramc.dev.journey.api.repository.journey.JourneyImageDetailEntity;
import com.github.nramc.dev.journey.api.repository.journey.JourneyImagesDetailsEntity;
import com.github.nramc.dev.journey.api.config.security.Visibility;
import lombok.experimental.UtilityClass;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@UtilityClass
public class JourneyData {
    public static final String NEW_JOURNEY_JSON = """
            {
              "name" : "First Flight Experience",
              "title" : "One of the most beautiful experience ever in my life",
              "description" : "Travelled first time for work deputation to Germany, Munich city",
              "category" : "Travel",
              "city" : "Munich",
              "country" : "Germany",
              "tags" : ["travel", "germany", "munich"],
              "thumbnail" : "https://example.com/thumbnail.png",
              "icon": "home",
              "location" : {
                "type": "Point",
                "coordinates": [48.183160038296585, 11.53090747669896]
              },
              "journeyDate": "2024-03-27"
            }
            """;
    public static final JourneyEntity JOURNEY_ENTITY = JourneyEntity.builder()
            .id("ecc76991-0137-4152-b3b2-efce70a37ed0")
            .name("First Flight Experience")
            .title("One of the most beautiful experience ever in my life")
            .description("Travelled first time for work deputation to Germany, Munich city")
            .category("Travel")
            .city("Munich")
            .country("Germany")
            .tags(List.of("travel", "germany", "munich"))
            .thumbnail("https://example.com/thumbnail.png")
            .location(Point.of(Position.of(48.183160038296585, 11.53090747669896)))
            .icon("home")
            .createdDate(LocalDate.of(2024, 3, 27))
            .journeyDate(LocalDate.of(2024, 3, 27))
            .visibilities(Set.of(Visibility.MYSELF))
            .createdBy("test-user")
            .build();

    public static final JourneyEntity JOURNEY_EXTENDED_ENTITY = JourneyEntity.builder()
            .id("ecc76991-0137-4152-b3b2-efce70a37ed0")
            .name("First Flight Experience")
            .title("One of the most beautiful experience ever in my life")
            .description("Travelled first time for work deputation to Germany, Munich city")
            .category("Travel")
            .city("Munich")
            .country("Germany")
            .tags(List.of("travel", "germany", "munich"))
            .thumbnail("https://example.com/thumbnail.png")
            .location(Point.of(Position.of(48.183160038296585, 11.53090747669896)))
            .icon("home")
            .createdDate(LocalDate.of(2024, 3, 27))
            .journeyDate(LocalDate.of(2024, 3, 27))
            .visibilities(Set.of(Visibility.MYSELF))
            .createdBy("test-user")
            .extended(JourneyExtendedEntity.builder()
                    .imagesDetails(getImagesDetailsEntity())
                    .build())
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
