package com.github.nramc.dev.journey.api.web.resources.rest.journeys;

import com.github.nramc.commons.geojson.domain.Point;
import com.github.nramc.commons.geojson.domain.Position;
import com.github.nramc.dev.journey.api.repository.journey.JourneyEntity;
import com.github.nramc.dev.journey.api.security.Visibility;
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
              "thumbnail" : "valid image id",
              "icon": "home",
              "location" : {
                "type": "Point",
                "coordinates": [48.183160038296585, 11.53090747669896]
              },
              "journeyDate": "2024-03-27"
            }
            """;
    public static final JourneyEntity JOURNEY_ENTITY = JourneyEntity.builder()
            .name("First Flight Experience")
            .title("One of the most beautiful experience ever in my life")
            .description("Travelled first time for work deputation to Germany, Munich city")
            .category("Travel")
            .city("Munich")
            .country("Germany")
            .tags(List.of("travel", "germany", "munich"))
            .thumbnail("valid image id")
            .location(Point.of(Position.of(48.183160038296585, 11.53090747669896)))
            .icon("home")
            .createdDate(LocalDate.of(2024, 3, 27))
            .journeyDate(LocalDate.of(2024, 3, 27))
            .visibilities(Set.of(Visibility.MYSELF))
            .createdBy("test-user")
            .build();
}
