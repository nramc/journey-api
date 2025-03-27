package com.github.nramc.dev.journey.api.repository.journey;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface JourneyRepository extends MongoRepository<JourneyEntity, String> {

}
