package com.github.nramc.dev.journey.api.repository;

import com.github.nramc.dev.journey.api.repository.entity.LocationEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface LocationRepository extends MongoRepository<LocationEntity, String> {
}
