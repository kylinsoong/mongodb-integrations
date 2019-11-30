package org.mongodb.sample;

import static org.mongodb.sample.App.*;
import static com.mongodb.client.model.Aggregates.*;
import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Accumulators.*;
import static com.mongodb.client.model.Sorts.*;
import static com.mongodb.client.model.Projections.*;
import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Accumulators.*;
import static com.mongodb.client.model.Sorts.*;
import static com.mongodb.client.model.Projections.*;

import java.util.Arrays;
import java.util.List;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.ConnectionString;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

public class AggregationApp {
	
	

	public static void main(String[] args) {
		
		MongoClient mongoClient = MongoClients.create(new ConnectionString(URI));
		MongoDatabase database = mongoClient.getDatabase(DB_NAME);
		MongoCollection<Document> collection = database.getCollection(COLL_NAME);
		
		List<? extends Bson> pipeline = Arrays.asList(group(and(eq("app", "$app"), eq("level", "$level")), sum("totalLogs", 1L)), project(fields(excludeId(), computed("app", "$_id.app"), computed("level", "$_id.level"), include("totalLogs"))), sort(descending("totalLogs")));
		
		MongoCursor<Document> cursor = collection.aggregate(pipeline).cursor();
		while (cursor.hasNext()) {
			System.out.println(cursor.next().toJson());
		}
		
		mongoClient.close();
	}

}
