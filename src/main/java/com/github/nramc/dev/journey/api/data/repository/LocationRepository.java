package com.github.nramc.dev.journey.api.data.repository;

import com.github.nramc.dev.journey.api.data.entity.LocationEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface LocationRepository extends MongoRepository<LocationEntity, String> {
}
