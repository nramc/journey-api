package com.github.nramc.dev.journey.api.core.journey;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.github.nramc.dev.journey.api.core.journey.security.Visibility;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import org.hibernate.validator.constraints.URL;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Builder(toBuilder = true)
public record Journey(
        @NotBlank String id,
        @NotBlank String name,
        @NotBlank String description,
        @NotEmpty List<String> tags,
        @NotBlank @URL String thumbnail,
        @NotNull @JsonFormat(pattern = "yyyy-MM-dd") LocalDate journeyDate,
        @NotNull @JsonFormat(pattern = "yyyy-MM-dd") LocalDate createdDate,
        @NotNull @Valid JourneyExtendedDetails extendedDetails,
        @NotEmpty Set<Visibility> visibilities,
        boolean isPublished) {
}
