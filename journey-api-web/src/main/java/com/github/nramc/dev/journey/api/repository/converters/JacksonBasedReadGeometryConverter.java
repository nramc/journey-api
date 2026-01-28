package com.github.nramc.dev.journey.api.repository.converters;

import com.github.nramc.geojson.domain.Geometry;
import org.bson.Document;
import org.jspecify.annotations.NonNull;
import org.springframework.core.convert.converter.Converter;
import tools.jackson.databind.ObjectMapper;

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
