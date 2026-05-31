package com.github.nramc.dev.journey.api.journey.web.journeys.update.publish;

import com.github.nramc.dev.journey.api.shared.domain.user.security.Visibility;
import com.github.nramc.dev.journey.api.shared.validation.ValidateVisibilities;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

import java.util.Set;

@Builder(toBuilder = true)
public record PublishJourneyRequest(
        @ValidateVisibilities Set<Visibility> visibilities,
        @NotBlank String thumbnail,
        boolean isPublished) {
}
