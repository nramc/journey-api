package com.github.nramc.dev.journey.api.migration.journeys;

import com.mongodb.MongoException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.bson.conversions.Bson;

@Slf4j
public class HelloWorld {

    public static void main(String[] args) {
        // Replace the uri string with your MongoDB deployment's connection string
        String uri = "<connection string uri>";

        try (MongoClient mongoClient = MongoClients.create(uri)) {
            MongoDatabase database = mongoClient.getDatabase("sample_mflix");

            MongoCollection<Document> collection = database.getCollection("movies");

            Document query = new Document().append("title", "Update Me");

            // Creates instructions to update the values of three document fields
            Bson updates = Updates.combine(
                    Updates.set("runtime", 99),
                    Updates.addToSet("genres", "Sports"),
                    Updates.currentTimestamp("lastUpdated"));

            // Instructs the driver to insert a new document if none match the query
            UpdateOptions options = new UpdateOptions().upsert(true);

            try {
                UpdateResult result = collection.updateOne(query, updates, options);
                log.info("Number of rows matched: {}", result.getModifiedCount());
                log.info("Modified rows count: {}", result.getModifiedCount());
                log.info("Upserted id: {}", result.getUpsertedId());
            } catch (MongoException ex) {
                log.error("Migration Failed", ex);
            }
        }
    }

}
