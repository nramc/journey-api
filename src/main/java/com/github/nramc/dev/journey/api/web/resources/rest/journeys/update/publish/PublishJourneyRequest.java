package com.github.nramc.dev.journey.api.web.resources.rest.journeys.update.publish;

import com.github.nramc.dev.journey.api.core.journey.security.Visibility;
import com.github.nramc.dev.journey.api.web.validation.ValidateVisibilities;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

import java.util.Set;

@Builder(toBuilder = true)
public record PublishJourneyRequest(
        @ValidateVisibilities Set<Visibility> visibilities,
        @NotBlank String thumbnail,
        boolean isPublished) {
}
