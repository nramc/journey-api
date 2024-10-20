package com.github.nramc.dev.journey.api.repository.journey;

import com.github.nramc.dev.journey.api.core.domain.AppUser;
import com.github.nramc.dev.journey.api.core.journey.security.Visibility;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;
import java.util.Set;

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

}
