package com.github.nramc.dev.journey.api.repository.journey;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class JourneyImagesDetailsEntity {
    private List<JourneyImageDetailEntity> images;

}
