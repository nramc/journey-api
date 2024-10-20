package com.github.nramc.dev.journey.api.repository.journey;

import com.github.nramc.dev.journey.api.config.TestContainersConfiguration;
import com.github.nramc.dev.journey.api.core.journey.security.Visibility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.github.nramc.dev.journey.api.web.resources.rest.journeys.JourneyData.AUTHENTICATED_USER;
import static com.github.nramc.dev.journey.api.web.resources.rest.journeys.JourneyData.JOURNEY_EXTENDED_ENTITY;
import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
@Import({TestContainersConfiguration.class, JourneyService.class})
class JourneyRepositoryTest {
    @Autowired
    private JourneyRepository journeyRepository;
    @Autowired
    private JourneyService journeyService;

    @BeforeEach
    void setUp() {
        journeyRepository.deleteAll();
    }

    @Test
    void context() {
        assertThat(journeyRepository).isNotNull();
        assertThat(journeyService).isNotNull();
    }


    @Test
    void getJourneyById() {
        journeyRepository.save(JOURNEY_EXTENDED_ENTITY);

        Optional<JourneyEntity> optionalJourney = journeyRepository.findById(JOURNEY_EXTENDED_ENTITY.getId());
        assertThat(optionalJourney).isNotEmpty();
    }

    @Test
    void findAllPublishedJourneys_whenJourneyExistsForUser_thenShouldInclude() {
        JourneyEntity journey = JOURNEY_EXTENDED_ENTITY.toBuilder()
                .createdBy(AUTHENTICATED_USER.username())
                .isPublished(true)
                .build();
        journeyRepository.save(journey);

        List<JourneyEntity> journeys = journeyService.findAllPublishedJourneys(AUTHENTICATED_USER, Set.of(Visibility.MYSELF));
        assertThat(journeys).isNotNull().hasSize(1);
    }

}
