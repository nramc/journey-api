package com.github.nramc.dev.journey.api.repository.journey;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Set;

public interface JourneyRepository extends MongoRepository<JourneyEntity, String> {
    //db.users.find(  } )

    @Query("""
            { $and: [
                { $or: [ {'createdBy' : ?1}, {'visibilities': {$in: ?0}} ] },
                { 'isPublished': {$in: ?2} },
                { $or: [
                    { 'name' : { $regex : '.*?3.*', '$options' : 'i' } },
                    { 'title' : { $regex : '.*?3.*', '$options' : 'i' } },
                    { 'description' : { $regex : '.*?3.*', '$options' : 'i' } }
                  ]
                }
              ]
            }
            """
    )
    Page<JourneyEntity> findAllBy(Set<String> visibilities, String username, Set<Boolean> publishedFlags, String searchText, Pageable pageable);
}
