package com.github.nramc.dev.journey.api.repository.journey;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class JourneyImageDetailEntity {
    private String url;
    private String assetId;
    String title;
    boolean isFavorite;
    boolean isThumbnail;

}
