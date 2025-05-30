package com.github.nramc.dev.journey.api.repository.journey.converter;

import com.github.nramc.dev.journey.api.repository.journey.JourneyEntity;
import com.github.nramc.dev.journey.api.repository.journey.JourneyGeoDetailsEntity;
import com.github.nramc.geojson.domain.Feature;
import org.apache.commons.collections4.map.HashedMap;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Optional;

public final class JourneyFeatureConverter {

    public static Feature toFeature(JourneyEntity entity) {
        return Feature.of(entity.getId(),
                Optional.of(entity).map(JourneyEntity::getGeoDetails).map(JourneyGeoDetailsEntity::getLocation).orElse(null),
                toProperties(entity)
        );
    }

    private static Map<String, Serializable> toProperties(JourneyEntity entity) {
        Map<String, Serializable> properties = new HashedMap<>();

        Optional<JourneyGeoDetailsEntity> optionalJourneyGeoDetails = Optional.of(entity)
                .map(JourneyEntity::getGeoDetails);

        properties.put("name", entity.getName());
        properties.put("description", entity.getDescription());
        properties.put("journeyDate", DateTimeFormatter.ISO_DATE.format(entity.getJourneyDate()));

        properties.put("category", optionalJourneyGeoDetails.map(JourneyGeoDetailsEntity::getCategory).orElse(StringUtils.EMPTY));
        properties.put("title", optionalJourneyGeoDetails.map(JourneyGeoDetailsEntity::getTitle).orElse(StringUtils.EMPTY));
        properties.put("city", optionalJourneyGeoDetails.map(JourneyGeoDetailsEntity::getCity).orElse(StringUtils.EMPTY));
        properties.put("country", optionalJourneyGeoDetails.map(JourneyGeoDetailsEntity::getCountry).orElse(StringUtils.EMPTY));

        properties.put("thumbnail", entity.getThumbnail());
        properties.put("tags", entity.getTags().toArray());

        return properties;
    }

    private JourneyFeatureConverter() {
        throw new IllegalStateException("Utility class");
    }

}
