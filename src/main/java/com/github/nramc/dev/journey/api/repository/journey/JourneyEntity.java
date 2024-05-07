package com.github.nramc.dev.journey.api.repository.journey;

import com.github.nramc.commons.geojson.domain.Geometry;
import com.github.nramc.dev.journey.api.security.Visibility;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Document("journey")
@Data
@NoArgsConstructor
@AllArgsConstructor
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
    private String icon;
    private Geometry location;
    private LocalDate createdDate;
    private String createdBy;
    private LocalDate journeyDate;
    private Boolean isPublished;
    private Set<Visibility> visibilities;
    private JourneyExtendedEntity extended;
}
