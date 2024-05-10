package com.github.nramc.dev.journey.api.repository.journey;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class JourneyImageDetailEntity {
    private String url;
    private String assetId;
    String title;
    boolean isFavorite;
    boolean isThumbnail;

}
