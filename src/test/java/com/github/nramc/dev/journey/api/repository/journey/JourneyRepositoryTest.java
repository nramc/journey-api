package com.github.nramc.dev.journey.api.repository.journey;

import com.github.nramc.dev.journey.api.config.TestContainersConfiguration;
import com.github.nramc.dev.journey.api.core.journey.Journey;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.Optional;

import static com.github.nramc.dev.journey.api.web.resources.rest.journeys.JourneyData.JOURNEY_ENTITY;
import static com.github.nramc.dev.journey.api.web.resources.rest.users.UsersData.AUTHENTICATED_APP_USER;
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
        journeyRepository.save(JOURNEY_ENTITY);

        Optional<JourneyEntity> optionalJourney = journeyRepository.findById(JOURNEY_ENTITY.getId());
        assertThat(optionalJourney).isNotEmpty();
    }

    @Test
    void findAllPublishedJourneys_whenJourneyExistsForUser_thenShouldInclude() {
        JourneyEntity journey = JOURNEY_ENTITY.toBuilder()
                .createdBy(AUTHENTICATED_APP_USER.username())
                .isPublished(true)
                .build();
        journeyRepository.save(journey);

        List<Journey> journeys = journeyService.findAllPublishedJourneys(AUTHENTICATED_APP_USER);
        assertThat(journeys).isNotNull().hasSize(1);
    }

}
