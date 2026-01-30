package com.github.nramc.dev.journey.api.repository.converters;

import com.github.nramc.geojson.domain.GeoJson;
import org.bson.Document;
import org.jspecify.annotations.NonNull;
import org.springframework.core.convert.converter.Converter;
import tools.jackson.databind.ObjectMapper;

public class JacksonBasedWriteGeoJsonConverter implements Converter<GeoJson, Document> {

    private final ObjectMapper objectMapper;

    public JacksonBasedWriteGeoJsonConverter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Document convert(@NonNull GeoJson source) {
        return objectMapper.convertValue(source, Document.class);
    }
}
