package com.github.nramc.dev.journey.api.repository.journey;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder(toBuilder = true)
public class JourneyImagesDetailsEntity {
    private List<JourneyImageDetailEntity> images;

}
