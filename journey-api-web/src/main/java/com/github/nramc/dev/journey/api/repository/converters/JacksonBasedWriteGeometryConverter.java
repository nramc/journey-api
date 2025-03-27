package com.github.nramc.dev.journey.api.repository.converters;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.nramc.geojson.domain.Geometry;
import org.bson.Document;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;

public class JacksonBasedWriteGeometryConverter implements Converter<Geometry, Document> {

    private final ObjectMapper objectMapper;

    public JacksonBasedWriteGeometryConverter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Document convert(@NonNull Geometry source) {
        return objectMapper.convertValue(source, Document.class);
    }
}
