package com.github.nramc.dev.journey.api.repository.journey;

import com.github.nramc.commons.geojson.domain.Geometry;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.List;

@Document("journey")
@Data
@Builder(toBuilder = true)
public class JourneyEntity {
    @Id
    private String id;
    private String name;
    private String title;
    private String description;
    private String category;
    private String city;
    private String country;
    private List<String> tags;
    private String thumbnail;
    private Geometry location;
    private LocalDate createdDate;
    private LocalDate journeyDate;
    private boolean isPublished;
    private JourneyExtendedEntity extended;
}
