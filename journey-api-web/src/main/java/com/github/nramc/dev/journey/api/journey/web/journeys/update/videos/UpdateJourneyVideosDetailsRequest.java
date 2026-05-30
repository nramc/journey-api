package com.github.nramc.dev.journey.api.journey.web.journeys.update.videos;

import lombok.Builder;

import java.util.List;

@Builder(toBuilder = true)
public record UpdateJourneyVideosDetailsRequest(List<VideoDetail> videos) {

    @Builder(toBuilder = true)
    public record VideoDetail(String videoId) {

    }
}
