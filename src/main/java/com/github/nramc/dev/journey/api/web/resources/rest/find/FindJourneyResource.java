package com.github.nramc.dev.journey.api.web.resources.rest.find;

import com.github.nramc.dev.journey.api.repository.journey.JourneyEntity;
import com.github.nramc.dev.journey.api.repository.journey.JourneyRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

import static com.github.nramc.dev.journey.api.web.resources.Resources.FIND_JOURNEY;
import static com.github.nramc.dev.journey.api.web.resources.Resources.FIND_JOURNEYS;

@RestController
@Slf4j
@RequiredArgsConstructor
@CrossOrigin(value = "*")
public class FindJourneyResource {
    private final JourneyRepository journeyRepository;

    @GetMapping(value = FIND_JOURNEY, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<FindJourneyResponse> findAndReturnJson(@Valid @NotBlank @PathVariable String id) {

        Optional<JourneyEntity> entityOptional = journeyRepository.findById(id);
        Optional<FindJourneyResponse> findJourneyResponse = entityOptional.map(FindJourneyConverter::convert);

        log.info("Journey exists? [{}]", findJourneyResponse.isPresent());
        return ResponseEntity.of(findJourneyResponse);
    }

    @GetMapping(value = FIND_JOURNEYS, produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<FindJourneyResponse> findAllAndReturnJson(
            @RequestParam(name = "pageIndex", defaultValue = "0") int pageIndex,
            @RequestParam(name = "pageSize", defaultValue = "10") int pageSize,
            @RequestParam(name = "sort", defaultValue = "createdDate") String sortColumn,
            @RequestParam(name = "order", defaultValue = "DESC") Sort.Direction sortOrder) {

        Pageable pageable = PageRequest.of(pageIndex, pageSize, Sort.by(sortOrder, sortColumn));

        Page<JourneyEntity> entityPage = journeyRepository.findAll(pageable);
        Page<FindJourneyResponse> responsePage = entityPage.map(FindJourneyConverter::convert);

        log.info("Journey exists:[{}] pages:[{}] total:[{}]",
                responsePage.hasContent(), responsePage.getTotalPages(), responsePage.getTotalElements());
        return responsePage;
    }

}
