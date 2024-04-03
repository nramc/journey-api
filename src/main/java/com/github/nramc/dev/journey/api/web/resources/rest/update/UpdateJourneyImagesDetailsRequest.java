package com.github.nramc.dev.journey.api.web.resources.rest.update;

import lombok.Builder;

import java.util.List;

@Builder(toBuilder = true)
public record UpdateJourneyImagesDetailsRequest(List<ImageDetail> images) {

    @Builder(toBuilder = true)
    public record ImageDetail(String url, String assetId) {

    }
}
