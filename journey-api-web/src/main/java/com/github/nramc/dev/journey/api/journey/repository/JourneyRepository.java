package com.github.nramc.dev.journey.api.journey.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface JourneyRepository extends MongoRepository<JourneyEntity, String> {

    List<JourneyEntity> findAllByIsPublished(boolean isPublished);

}
