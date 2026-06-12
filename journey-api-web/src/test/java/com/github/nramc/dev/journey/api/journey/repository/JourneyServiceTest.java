package com.github.nramc.dev.journey.api.journey.repository;

import com.github.nramc.dev.journey.api.account.web.users.UsersData;
import com.github.nramc.dev.journey.api.infrastructure.config.TestContainersConfiguration;
import com.github.nramc.dev.journey.api.journey.domain.Journey;
import com.github.nramc.dev.journey.api.journey.web.journeys.JourneyData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.mongodb.test.autoconfigure.DataMongoTest;
import org.springframework.context.annotation.Import;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import static com.github.nramc.dev.journey.api.shared.domain.user.security.Visibility.AUTHENTICATED_USER;
import static com.github.nramc.dev.journey.api.shared.domain.user.security.Visibility.GUEST;
import static com.github.nramc.dev.journey.api.shared.domain.user.security.Visibility.MYSELF;
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

    @Test
    void getAnniversariesInNextDays_shouldIncludeOnlyPublishedOwnOrVisibleJourneys() {
        String username = UsersData.AUTHENTICATED_APP_USER.username();
        LocalDate anniversaryDate = LocalDate.now().plusDays(5).minusYears(1);
        journeyRepository.save(VALID_JOURNEY.toBuilder()
                .id("OWN_PUBLISHED")
                .createdBy(username)
                .isPublished(true)
                .visibilities(Set.of(MYSELF))
                .journeyDate(anniversaryDate)
                .build());
        journeyRepository.save(VALID_JOURNEY.toBuilder()
                .id("SHARED_VISIBLE")
                .createdBy("other.user@example.com")
                .isPublished(true)
                .visibilities(Set.of(AUTHENTICATED_USER))
                .journeyDate(anniversaryDate)
                .build());
        journeyRepository.save(VALID_JOURNEY.toBuilder()
                .id("SHARED_NOT_VISIBLE")
                .createdBy("other.user@example.com")
                .isPublished(true)
                .visibilities(Set.of(GUEST))
                .journeyDate(anniversaryDate)
                .build());
        journeyRepository.save(VALID_JOURNEY.toBuilder()
                .id("OWN_UNPUBLISHED")
                .createdBy(username)
                .isPublished(false)
                .visibilities(Set.of(MYSELF))
                .journeyDate(anniversaryDate)
                .build());

        List<Journey> journeys = journeyService.getAnniversariesInNextDays(UsersData.AUTHENTICATED_APP_USER, 5);

        assertThat(journeys)
                .extracting(Journey::id)
                .containsExactlyInAnyOrder("OWN_PUBLISHED", "SHARED_VISIBLE");
    }

    @Test
    void getAnniversariesInNextDays_shouldReturnOnlyJourneysInsideRequestedDayWindow() {
        String username = UsersData.AUTHENTICATED_APP_USER.username();
        journeyRepository.save(VALID_JOURNEY.toBuilder()
                .id("INSIDE_WINDOW")
                .createdBy(username)
                .isPublished(true)
                .visibilities(Set.of(MYSELF))
                .journeyDate(LocalDate.now().plusDays(3).minusYears(2))
                .build());
        journeyRepository.save(VALID_JOURNEY.toBuilder()
                .id("OUTSIDE_WINDOW")
                .createdBy(username)
                .isPublished(true)
                .visibilities(Set.of(MYSELF))
                .journeyDate(LocalDate.now().plusDays(40).minusYears(2))
                .build());

        List<Journey> journeys = journeyService.getAnniversariesInNextDays(UsersData.AUTHENTICATED_APP_USER, 7);

        assertThat(journeys)
                .extracting(Journey::id)
                .containsExactly("INSIDE_WINDOW");
    }

}
