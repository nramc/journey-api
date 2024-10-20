package com.github.nramc.dev.journey.api.repository.journey.converter;

import com.github.nramc.commons.geojson.domain.Feature;
import com.github.nramc.dev.journey.api.repository.journey.JourneyEntity;
import com.github.nramc.dev.journey.api.repository.journey.JourneyExtendedEntity;
import com.github.nramc.dev.journey.api.repository.journey.JourneyGeoDetailsEntity;
import lombok.experimental.UtilityClass;
import org.apache.commons.collections4.map.HashedMap;

import java.io.Serializable;
import java.util.Map;
import java.util.Optional;

@UtilityClass
public class JourneyFeatureConverter {

    public static Feature toFeature(JourneyEntity entity) {
        return Feature.of(entity.getId(), entity.getExtended().getGeoDetails().getLocation(), toProperties(entity));
    }

    private static Map<String, Serializable> toProperties(JourneyEntity entity) {
        Map<String, Serializable> properties = new HashedMap<>();

        Optional<JourneyGeoDetailsEntity> optionalJourneyGeoDetails = Optional.of(entity)
                .map(JourneyEntity::getExtended).map(JourneyExtendedEntity::getGeoDetails);

        properties.put("name", entity.getName());
        properties.put("description", entity.getDescription());

        properties.put("category", optionalJourneyGeoDetails.map(JourneyGeoDetailsEntity::getCategory).orElse(null));
        properties.put("title", optionalJourneyGeoDetails.map(JourneyGeoDetailsEntity::getTitle).orElse(null));
        properties.put("city", optionalJourneyGeoDetails.map(JourneyGeoDetailsEntity::getCity).orElse(null));
        properties.put("country", optionalJourneyGeoDetails.map(JourneyGeoDetailsEntity::getCountry).orElse(null));
        properties.put("location", optionalJourneyGeoDetails.map(JourneyGeoDetailsEntity::getLocation).orElse(null));

        properties.put("thumbnail", entity.getThumbnail());
        properties.put("tags", entity.getTags().toArray());

        return properties;
    }

}
