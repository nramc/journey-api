package com.github.nramc.dev.journey.api.repository.converters;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.nramc.geojson.domain.Geometry;
import org.bson.Document;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;

public class JacksonBasedReadGeometryConverter implements Converter<Document, Geometry> {

    private final ObjectMapper objectMapper;

    public JacksonBasedReadGeometryConverter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Geometry convert(@NonNull Document source) {
        return objectMapper.convertValue(source, Geometry.class);
    }
}
