package com.github.nramc.dev.journey.api.repository.journey;

import com.github.nramc.dev.journey.api.core.journey.security.Visibility;
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
    private String description;
    private List<String> tags;
    private String thumbnail;
    private LocalDate createdDate;
    private String createdBy;
    private LocalDate journeyDate;
    private Boolean isPublished;
    private Set<Visibility> visibilities;
    private JourneyExtendedEntity extended;
}
