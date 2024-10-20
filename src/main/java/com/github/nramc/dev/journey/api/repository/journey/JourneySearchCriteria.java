package com.github.nramc.dev.journey.api.repository.journey;

import com.github.nramc.dev.journey.api.core.domain.AppUser;
import com.github.nramc.dev.journey.api.core.journey.security.Visibility;
import lombok.Builder;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Builder(toBuilder = true)
public record JourneySearchCriteria(
        Set<Visibility> visibilities,
        AppUser appUser,
        Set<Boolean> publishedFlags,
        String searchText,
        List<String> tags,
        String cityText,
        String countryText,
        String categoryText,
        LocalDate journeyStartDate,
        LocalDate journeyEndDate) {

}
