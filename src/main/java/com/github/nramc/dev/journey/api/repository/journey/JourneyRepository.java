package com.github.nramc.dev.journey.api.repository.journey;

import com.github.nramc.dev.journey.api.security.Visibility;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
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
                },
                { $or: [
                    { $expr: { $eq: [?4, []] } },
                    { 'tags': { $in: ?4 } }
                  ]
                }
              ]
            }
            """
    )
    Page<JourneyEntity> findAllBy(
            Set<Visibility> visibilities,
            String username,
            Set<Boolean> publishedFlags,
            String searchText,
            List<String> tags,
            Pageable pageable);
}
