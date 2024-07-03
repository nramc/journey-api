package com.github.nramc.dev.journey.api.repository.journey;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class JourneyImageDetailEntity {
    private String url;
    private String assetId;
    private String publicId;
    String title;
    LocalDate eventDate;
    boolean isFavorite;
    boolean isThumbnail;

}
