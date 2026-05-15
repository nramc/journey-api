package com.github.nramc.dev.journey.api.infrastructure.mongodb;

import com.github.nramc.dev.journey.api.infrastructure.mongodb.converters.JacksonBasedReadGeoJsonConverter;
import com.github.nramc.dev.journey.api.infrastructure.mongodb.converters.JacksonBasedReadGeometryConverter;
import com.github.nramc.dev.journey.api.infrastructure.mongodb.converters.JacksonBasedWriteGeoJsonConverter;
import com.github.nramc.dev.journey.api.infrastructure.mongodb.converters.JacksonBasedWriteGeometryConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

@Configuration
public class MongoConfig {

    @Bean
    public MongoCustomConversions customConversions(ObjectMapper objectMapper) {
        return new MongoCustomConversions(List.of(
                // GeoJson
                new JacksonBasedReadGeoJsonConverter(objectMapper),
                new JacksonBasedWriteGeoJsonConverter(objectMapper),
                // Geometry
                new JacksonBasedReadGeometryConverter(objectMapper),
                new JacksonBasedWriteGeometryConverter(objectMapper)
        ));
    }
}
