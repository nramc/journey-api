package com.github.nramc.dev.journey.api.repository.journey;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class JourneyExtendedEntity {
    private JourneyGeoDetailsEntity geoDetails;
    private JourneyImagesDetailsEntity imagesDetails;
    private JourneyVideosDetailsEntity videosDetails;

}
