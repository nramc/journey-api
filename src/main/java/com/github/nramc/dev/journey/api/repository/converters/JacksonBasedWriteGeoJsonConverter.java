package com.github.nramc.dev.journey.api.repository.converters;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.nramc.commons.geojson.domain.GeoJson;
import org.bson.Document;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;

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
