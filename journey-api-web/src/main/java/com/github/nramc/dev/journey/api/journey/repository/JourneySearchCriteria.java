package com.github.nramc.dev.journey.api.journey.repository;

import com.github.nramc.dev.journey.api.shared.domain.AppUser;
import com.github.nramc.dev.journey.api.shared.domain.Visibility;
import lombok.Builder;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Builder(toBuilder = true)
public record JourneySearchCriteria(
        Set<Visibility> visibilities,
        AppUser appUser,
        List<String> ids,
        Set<Boolean> publishedFlags,
        String searchText,
        List<String> tags,
        List<String> cities,
        List<String> countries,
        List<String> categories,
        LocalDate journeyDateFrom,
        LocalDate journeyDateUpTo,
        List<Long> journeyYears,
        Integer journeyDaysUpTo) {

}
