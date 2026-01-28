package com.github.nramc.dev.journey.api.repository.converters;

import com.github.nramc.geojson.domain.Geometry;
import org.bson.Document;
import org.jspecify.annotations.NonNull;
import org.springframework.core.convert.converter.Converter;
import tools.jackson.databind.ObjectMapper;

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
