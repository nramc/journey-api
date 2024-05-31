package com.github.nramc.dev.journey.api.web.resources.rest.timeline;

import lombok.Builder;

import java.util.List;
import java.util.Map;

@Builder(toBuilder = true)
public record TimelineData(
        String heading,
        List<TimelineImage> images
) {

    @Builder(toBuilder = true)
    public record TimelineImage(
            String title,
            String src,
            String caption,
            Map<String, String> args
    ) {
    }

}
