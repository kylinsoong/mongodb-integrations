package org.mongodb.sample;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.bson.Document;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.ReadPreference;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;

public class MongoClientSample {
	
	protected void mongoCollectionInstance() {
		
		MongoClient mongoClient = mongoClientInstance();
		MongoDatabase database = mongoClient.getDatabase("mflix");
		
		MongoCollection<Document> collection = database.getCollection("movies");
		MongoIterable<Document> cursor = collection.find().skip(10).limit(20);
		List<Document> documents = new ArrayList<>();
		cursor.into(documents);
		
		System.out.println(documents);
		
		mongoClient.close();
	}
	
	
	protected void mongoDatabaseInstance() {
	    
	    MongoClient mongoClient = mongoClientInstance();
	    
	    MongoIterable<String> databaseIterable = mongoClient.listDatabaseNames();
	    
	    for (String name : databaseIterable) {
	    	System.out.println(name);
	    }
	    
	    MongoDatabase database = mongoClient.getDatabase("mflix");
	    
	    ReadPreference readPreference = database.getReadPreference();
	    
	    System.out.println(readPreference.getName());
	    
	    mongoClient.close();
	}
	
	protected MongoClient mongoClientInstance() {
		
		String uri = "mongodb+srv://kylin:mongo@mflix-5ctvg.gcp.mongodb.net/mflix";
		
		ConnectionString connectionString = new ConnectionString(uri);
	    MongoClientSettings clientSettings =
	        MongoClientSettings.builder()
	            .applyConnectionString(connectionString)
	            .applicationName("mflix")
	            .applyToConnectionPoolSettings(
	                builder -> builder.maxWaitTime(1000, TimeUnit.MILLISECONDS))
	            .build();
	    
	    MongoClient mongoClient = MongoClients.create(clientSettings);
		
		return mongoClient;
	}

	public static void main(String[] args) {
		
		MongoClientSample sample = new MongoClientSample();
		
		sample.mongoCollectionInstance();

	}

}
