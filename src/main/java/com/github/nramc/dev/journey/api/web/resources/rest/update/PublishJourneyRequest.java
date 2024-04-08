package com.github.nramc.dev.journey.api.web.resources.rest.update;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder(toBuilder = true)
public record PublishJourneyRequest(
        @NotBlank String thumbnail,
        boolean isPublished) {
}
