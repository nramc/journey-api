package com.github.nramc.dev.journey.api.repository.journey;

import com.github.nramc.dev.journey.api.config.TestContainersConfiguration;
import com.github.nramc.dev.journey.api.core.journey.Journey;
import com.github.nramc.dev.journey.api.web.resources.rest.journeys.JourneyData;
import com.github.nramc.dev.journey.api.web.resources.rest.users.UsersData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
@Import({TestContainersConfiguration.class, JourneyService.class})
class JourneyServiceTest {
    private static final JourneyEntity VALID_JOURNEY = JourneyData.JOURNEY_ENTITY.toBuilder().build();
    @Autowired
    private JourneyService journeyService;
    @Autowired
    private JourneyRepository journeyRepository;

    @BeforeEach
    void setUp() {
        journeyRepository.deleteAll();
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4, 5, 6, 7, 14, 21, 30, 60, 90, 120, 150, 180, 210, 240, 270, 300, 330})
    void getAnniversariesInNextDays_whenDaysProvided_shouldProvideResult(int daysUpTo) {
        IntStream.range(0, 5).forEach(index -> journeyRepository.save(
                VALID_JOURNEY.toBuilder()
                        .id("ID_" + index)
                        .journeyDate(LocalDate.now().plusDays(daysUpTo))
                        .isPublished(true)
                        .build())
        );

        List<Journey> journeys = journeyService.getAnniversariesInNextDays(UsersData.AUTHENTICATED_APP_USER, daysUpTo);
        assertThat(journeys).isNotEmpty().hasSize(5);
    }

}