package com.github.nramc.dev.journey.api.repository.converters;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.nramc.commons.geojson.domain.GeoJson;
import org.bson.Document;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;

public class JacksonBasedReadGeoJsonConverter implements Converter<Document, GeoJson> {

    private final ObjectMapper objectMapper;

    public JacksonBasedReadGeoJsonConverter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public GeoJson convert(@NonNull Document source) {
        return objectMapper.convertValue(source, GeoJson.class);
    }
}
