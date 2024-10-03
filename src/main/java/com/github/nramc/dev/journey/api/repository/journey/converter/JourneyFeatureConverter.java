package com.github.nramc.dev.journey.api.repository.journey.converter;

import com.github.nramc.commons.geojson.domain.Feature;
import com.github.nramc.dev.journey.api.repository.journey.JourneyEntity;
import lombok.experimental.UtilityClass;
import org.apache.commons.collections4.map.HashedMap;

import java.io.Serializable;
import java.util.Map;

@UtilityClass
public class JourneyFeatureConverter {

    public static Feature toFeature(JourneyEntity entity) {
        return Feature.of(entity.getId(), entity.getLocation(), toProperties(entity));
    }

    private static Map<String, Serializable> toProperties(JourneyEntity entity) {
        Map<String, Serializable> properties = new HashedMap<>();

        properties.put("name", entity.getName());
        properties.put("category", entity.getCategory());
        properties.put("title", entity.getTitle());
        properties.put("description", entity.getDescription());

        properties.put("city", entity.getCity());
        properties.put("country", entity.getCountry());
        properties.put("location", entity.getLocation());
        properties.put("thumbnail", entity.getThumbnail());
        properties.put("icon", entity.getIcon());

        properties.put("tags", entity.getTags().toArray());

        return properties;
    }

}
