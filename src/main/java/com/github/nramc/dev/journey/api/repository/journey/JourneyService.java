package com.github.nramc.dev.journey.api.repository.journey;

import com.github.nramc.dev.journey.api.core.domain.AppUser;
import com.github.nramc.dev.journey.api.core.domain.data.DataPageable;
import com.github.nramc.dev.journey.api.core.journey.Journey;
import com.github.nramc.dev.journey.api.core.journey.security.Visibility;
import com.github.nramc.dev.journey.api.repository.journey.converter.JourneyConverter;
import com.github.nramc.dev.journey.api.web.resources.rest.auth.utils.AuthUtils;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.support.PageableExecutionUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.github.nramc.dev.journey.api.repository.journey.JourneyCriteriaUtils.getCriteriaWhenDateRangeFallsCrossMonths;
import static com.github.nramc.dev.journey.api.repository.journey.JourneyCriteriaUtils.getCriteriaWhenDateRangeFallsUnderSameMonths;
import static com.github.nramc.dev.journey.api.repository.journey.JourneyCriteriaUtils.transformSearchCriteria;

@RequiredArgsConstructor
public class JourneyService {
    private final MongoTemplate mongoTemplate;


    public List<JourneyEntity> findAllPublishedJourneys(AppUser user, Set<Visibility> visibilities) {

        Criteria publishedJourneysCriteria = Criteria.where("isPublished").is(true);
        Criteria userOwnedJourneysCriteria = Criteria.where("createdBy").is(user.username());
        Criteria journeyConntainsUserVisibilityCriteria = Criteria.where("visibility").in(visibilities);

        Query query = Query.query(publishedJourneysCriteria.orOperator(userOwnedJourneysCriteria, journeyConntainsUserVisibilityCriteria));

        return mongoTemplate.query(JourneyEntity.class)
                .matching(query)
                .all();
    }

    public List<Journey> getAnniversariesInNextDays(AppUser user, int daysAhead) {
        List<Criteria> criteriaList = new ArrayList<>();
        criteriaList.add(Criteria.where("isPublished").is(true).orOperator(
                Criteria.where("createdBy").is(user.username()),
                Criteria.where("visibilities").in(AuthUtils.getVisibilityFromRole(user.roles())))
        );

        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(daysAhead);

        if (startDate.getMonthValue() == endDate.getMonthValue()) {
            criteriaList.add(getCriteriaWhenDateRangeFallsUnderSameMonths(startDate, endDate));
        } else {
            criteriaList.add(getCriteriaWhenDateRangeFallsCrossMonths(startDate, endDate));
        }

        Criteria combinedCriteria = new Criteria().andOperator(criteriaList.toArray(new Criteria[0]));
        List<JourneyEntity> results = mongoTemplate.find(new Query(combinedCriteria), JourneyEntity.class);
        return CollectionUtils.emptyIfNull(results).stream().map(JourneyConverter::convert).toList();
    }

    public List<Journey> findAllJourneys(JourneySearchCriteria searchCriteria) {
        Criteria combinedCriteria = transformSearchCriteria(searchCriteria);
        List<JourneyEntity> results = mongoTemplate.find(new Query(combinedCriteria), JourneyEntity.class);
        return CollectionUtils.emptyIfNull(results).stream().map(JourneyConverter::convert).toList();
    }

    public DataPageable<Journey> findAllJourneysWithPagination(JourneySearchCriteria searchCriteria, PagingProperty pagingProperty) {
        Criteria combinedCriteria = transformSearchCriteria(searchCriteria);
        Query query = new Query(combinedCriteria);

        // Pagination &  setup
        PageRequest pageable = PageRequest.of(pagingProperty.pageIndex(), pagingProperty.pageSize());
        query.with(pageable);

        Sort.Direction direction = "desc".equalsIgnoreCase(pagingProperty.sortDirection()) ? Sort.Direction.DESC : Sort.Direction.ASC;
        query.with(Sort.by(direction, pagingProperty.sortColumn()));

        // Fetching the results
        List<JourneyEntity> results = mongoTemplate.find(query, JourneyEntity.class);

        Page<JourneyEntity> journeyEntityPage = PageableExecutionUtils.getPage(
                results,
                pageable,
                () -> mongoTemplate.count(new Query(combinedCriteria), JourneyEntity.class));
        Page<Journey> journeyPage = journeyEntityPage.map(JourneyConverter::convert);
        return DataPageable.<Journey>builder()
                .content(journeyPage.getContent())
                .numberOfElements(journeyPage.getNumberOfElements())
                .totalElements(journeyPage.getTotalElements())
                .totalPages(journeyPage.getTotalPages())
                .pageNumber(pagingProperty.pageIndex())
                .pageSize(pagingProperty.pageSize())
                .build();
    }


}
