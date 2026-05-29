package com.github.nramc.dev.journey.api.journey.web.timeline;

import com.github.nramc.dev.journey.api.journey.domain.Journey;
import lombok.Builder;

import java.util.List;

@Builder(toBuilder = true)
public record TimelineData(
        String heading,
        List<Journey> journeys
) {
}
