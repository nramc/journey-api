package com.github.nramc.dev.journey.api.web.resources.rest.journeys.update.images;

import com.github.nramc.dev.journey.api.repository.journey.JourneyEntity;
import com.github.nramc.dev.journey.api.repository.journey.JourneyExtendedEntity;
import com.github.nramc.dev.journey.api.repository.journey.JourneyImageDetailEntity;
import com.github.nramc.dev.journey.api.repository.journey.JourneyImagesDetailsEntity;
import com.github.nramc.dev.journey.api.repository.journey.JourneyRepository;
import com.github.nramc.dev.journey.api.gateway.cloudinary.CloudinaryService;
import com.github.nramc.dev.journey.api.core.journey.Journey;
import com.github.nramc.dev.journey.api.repository.journey.converter.JourneyConverter;
import com.github.nramc.dev.journey.api.web.resources.rest.doc.RestDocCommonResponse;
import com.github.nramc.dev.journey.api.web.resources.rest.journeys.update.UpdateJourneyConverter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.github.nramc.dev.journey.api.web.resources.Resources.MediaType.UPDATE_JOURNEY_IMAGES_DETAILS;
import static com.github.nramc.dev.journey.api.web.resources.Resources.UPDATE_JOURNEY;

@RestController
@Slf4j
@RequiredArgsConstructor
public class UpdateJourneyImagesDetailsResource {
    private final JourneyRepository journeyRepository;
    private final CloudinaryService cloudinaryService;

    @RestDocCommonResponse
    @Operation(summary = "Update Images details of Journey", tags = {"Update Journey"})
    @ApiResponse(responseCode = "200", description = "Journey details updated successfully")
    @PutMapping(value = UPDATE_JOURNEY, consumes = UPDATE_JOURNEY_IMAGES_DETAILS, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Journey> updateImagesDetails(@RequestBody @Valid UpdateJourneyImagesDetailsRequest request, @PathVariable String id) {
        JourneyEntity entity = journeyRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Given ID does not exists, can't update images info"));

        JourneyEntity journey = UpdateJourneyConverter.extendWithImagesDetails(request, entity);

        deleteImagesFromCloudinaryIfRequired(entity, request.images());

        JourneyEntity journeyEntity = journeyRepository.save(journey);

        log.info("Journey's image information saved successfully with id:{}", journeyEntity.getId());
        return ResponseEntity.status(HttpStatus.OK).body(JourneyConverter.convert(journeyEntity));
    }

    private void deleteImagesFromCloudinaryIfRequired(JourneyEntity journey, List<UpdateJourneyImagesDetailsRequest.ImageDetail> newImageDetails) {
        List<JourneyImageDetailEntity> currentImagesEntities = Optional.of(journey).map(JourneyEntity::getExtended).map(JourneyExtendedEntity::getImagesDetails)
                .map(JourneyImagesDetailsEntity::getImages).orElse(Collections.emptyList());
        Set<String> currentImages = CollectionUtils.emptyIfNull(currentImagesEntities)
                .stream().map(JourneyImageDetailEntity::getAssetId).collect(Collectors.toSet());
        Set<String> newImages = CollectionUtils.emptyIfNull(newImageDetails).stream().map(UpdateJourneyImagesDetailsRequest.ImageDetail::assetId).collect(Collectors.toSet());
        boolean isChangeExists = currentImages.removeAll(newImages);
        if (isChangeExists) {
            currentImages.forEach(cloudinaryService::deleteImage);
        }
    }
}
