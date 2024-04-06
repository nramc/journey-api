package com.github.nramc.dev.journey.api.repository.journey;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class JourneyExtendedEntity {
    private JourneyGeoDetailsEntity geoDetails;
    private JourneyImagesDetailsEntity imagesDetails;
    private JourneyVideosDetailsEntity videosDetails;

}
