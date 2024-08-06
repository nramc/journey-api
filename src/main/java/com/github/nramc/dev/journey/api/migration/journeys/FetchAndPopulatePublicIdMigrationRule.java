package com.github.nramc.dev.journey.api.migration.journeys;

import com.cloudinary.api.ApiResponse;
import com.github.nramc.dev.journey.api.repository.journey.JourneyEntity;
import com.github.nramc.dev.journey.api.repository.journey.JourneyExtendedEntity;
import com.github.nramc.dev.journey.api.repository.journey.JourneyImageDetailEntity;
import com.github.nramc.dev.journey.api.repository.journey.JourneyImagesDetailsEntity;
import com.github.nramc.dev.journey.api.repository.journey.JourneyRepository;
import com.github.nramc.dev.journey.api.gateway.cloudinary.CloudinaryService;
import com.github.nramc.dev.journey.api.web.exceptions.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
public class FetchAndPopulatePublicIdMigrationRule implements Runnable {
    private final JourneyRepository journeyRepository;
    private final CloudinaryService cloudinaryService;


    @Override
    public void run() {
        List<JourneyEntity> journeys = journeyRepository.findAll();
        CollectionUtils.emptyIfNull(journeys).stream()
                .filter(journeyEntity -> Objects.nonNull(journeyEntity.getExtended()))
                .filter(journeyEntity -> Objects.nonNull(journeyEntity.getExtended().getImagesDetails()))
                .filter(journeyEntity -> journeyEntity.getExtended().getImagesDetails().getImages().stream()
                        .anyMatch(journeyImageDetailEntity -> StringUtils.isBlank(journeyImageDetailEntity.getPublicId())))
                .forEach(this::migrateJourney);
    }

    private void migrateJourney(JourneyEntity journeyEntity) {

        List<JourneyImageDetailEntity> imageDetailEntities = journeyEntity.getExtended()
                .getImagesDetails()
                .getImages()
                .stream()
                .map(this::fetchAndPopulatePublicId)
                .toList();

        JourneyImagesDetailsEntity imagesDetailsEntity = journeyEntity.getExtended()
                .getImagesDetails().toBuilder()
                .images(imageDetailEntities)
                .build();
        JourneyExtendedEntity extendedEntity = journeyEntity.getExtended().toBuilder()
                .imagesDetails(imagesDetailsEntity)
                .build();

        journeyRepository.save(journeyEntity.toBuilder().extended(extendedEntity).build());
    }

    private JourneyImageDetailEntity fetchAndPopulatePublicId(JourneyImageDetailEntity journeyImageDetailEntity) {
        try {
            ApiResponse apiResponse = cloudinaryService.getResource(journeyImageDetailEntity.getAssetId());
            return journeyImageDetailEntity
                    .toBuilder()
                    .publicId(apiResponse.get("public_id").toString())
                    .build();
        } catch (Exception ex) {
            log.error("Unable migrate rule.", ex);
            throw new BusinessException("Unable migrate rule", ex.getMessage());
        }
    }


}
