package com.github.nramc.dev.journey.api.web.resources.rest.find;

import com.github.nramc.dev.journey.api.config.security.Authority;
import com.github.nramc.dev.journey.api.repository.journey.JourneyEntity;
import com.github.nramc.dev.journey.api.repository.journey.JourneyRepository;
import com.github.nramc.dev.journey.api.web.dto.Journey;
import com.github.nramc.dev.journey.api.web.dto.converter.JourneyConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.github.nramc.dev.journey.api.web.resources.Resources.FIND_JOURNEYS;

@RestController
@Slf4j
@RequiredArgsConstructor
@CrossOrigin(value = "*")
public class FindJourneyByQueryResource {
    private final JourneyRepository journeyRepository;

    @GetMapping(value = FIND_JOURNEYS, produces = MediaType.APPLICATION_JSON_VALUE)
    @Secured({Authority.MAINTAINER})
    public Page<Journey> find(
            @RequestParam(name = "pageIndex", defaultValue = "0") int pageIndex,
            @RequestParam(name = "pageSize", defaultValue = "10") int pageSize,
            @RequestParam(name = "sort", defaultValue = "createdDate") String sortColumn,
            @RequestParam(name = "order", defaultValue = "DESC") Sort.Direction sortOrder) {

        Pageable pageable = PageRequest.of(pageIndex, pageSize, Sort.by(sortOrder, sortColumn));

        Page<JourneyEntity> entityPage = journeyRepository.findAll(pageable);
        Page<Journey> responsePage = entityPage.map(JourneyConverter::convert);

        log.info("Journey exists:[{}] pages:[{}] total:[{}]",
                responsePage.hasContent(), responsePage.getTotalPages(), responsePage.getTotalElements());
        return responsePage;
    }
}
