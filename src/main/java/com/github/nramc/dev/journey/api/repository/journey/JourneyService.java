package com.github.nramc.dev.journey.api.repository.journey;

import com.github.nramc.dev.journey.api.core.domain.AppUser;
import com.github.nramc.dev.journey.api.core.domain.data.DataPageable;
import com.github.nramc.dev.journey.api.core.journey.Journey;
import com.github.nramc.dev.journey.api.core.journey.security.Visibility;
import com.github.nramc.dev.journey.api.repository.journey.converter.JourneyConverter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static java.util.regex.Pattern.CASE_INSENSITIVE;
import static java.util.regex.Pattern.compile;

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

    public DataPageable<Journey> findAllJourneysWithPagination(JourneySearchCriteria searchCriteria, PagingProperty pagingProperty) {
        List<Criteria> criteriaList = new ArrayList<>();

        criteriaList.add(
                new Criteria().orOperator(
                        Criteria.where("createdBy").is(searchCriteria.appUser().username()),
                        Criteria.where("visibilities").in(searchCriteria.visibilities())
                )
        );
        criteriaList.add(
                Criteria.where("isPublished").in(searchCriteria.publishedFlags())
        );
        if (StringUtils.isNotEmpty(searchCriteria.searchText())) {
            criteriaList.add(new Criteria().orOperator(
                    Criteria.where("name").regex(compile(".*" + searchCriteria.searchText() + ".*", CASE_INSENSITIVE)),
                    Criteria.where("description").regex(compile(".*" + searchCriteria.searchText() + ".*", CASE_INSENSITIVE))
            ));
        }
        if (CollectionUtils.isNotEmpty(searchCriteria.tags())) {
            criteriaList.add(Criteria.where("tags").in(searchCriteria.tags()));
        }
        if (StringUtils.isNotEmpty(searchCriteria.cityText())) {
            criteriaList.add(Criteria.where("extended.geoDetails.city").is(searchCriteria.cityText()));
        }
        if (StringUtils.isNotEmpty(searchCriteria.countryText())) {
            criteriaList.add(Criteria.where("extended.geoDetails.country").is(searchCriteria.countryText()));
        }
        if (StringUtils.isNotEmpty(searchCriteria.categoryText())) {
            criteriaList.add(Criteria.where("extended.geoDetails.category").is(searchCriteria.categoryText()));
        }
        if (searchCriteria.journeyStartDate() != null) {
            criteriaList.add(Criteria.where("journeyDate").gte(searchCriteria.journeyStartDate()));
        }
        if (searchCriteria.journeyEndDate() != null) {
            criteriaList.add(Criteria.where("journeyDate").lte(searchCriteria.journeyEndDate()));
        }

        Criteria combinedCriteria = new Criteria().andOperator(criteriaList.toArray(new Criteria[0]));
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
