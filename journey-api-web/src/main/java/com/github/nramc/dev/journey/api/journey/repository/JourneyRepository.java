package com.github.nramc.dev.journey.api.journey.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface JourneyRepository extends MongoRepository<JourneyEntity, String> {

}
