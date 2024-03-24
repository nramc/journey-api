package com.github.nramc.dev.journey.api.repository.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("location")
@Data
public class LocationEntity {
    @Id
    private String id;

    private String name;

    public LocationEntity(String name) {
        this.name = name;
    }
}
