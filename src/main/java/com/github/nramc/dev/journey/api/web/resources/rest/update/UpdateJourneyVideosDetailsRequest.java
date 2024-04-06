package com.github.nramc.dev.journey.api.web.resources.rest.update;

import lombok.Builder;

import java.util.List;

@Builder(toBuilder = true)
public record UpdateJourneyVideosDetailsRequest(List<VideoDetail> videos) {

    @Builder(toBuilder = true)
    public record VideoDetail(String videoId) {

    }
}
