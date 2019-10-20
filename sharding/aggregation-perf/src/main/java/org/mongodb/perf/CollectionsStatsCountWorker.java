package org.mongodb.perf;

import org.bson.Document;

import com.mongodb.ConnectionString;
import com.mongodb.client.ListCollectionsIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;

public class CollectionsStatsCountWorker implements Worker {
	
	private String uri;
	
	private String database;

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public String getDb() {
		return database;
	}

	public void setDb(String db) {
		this.database = db;
	}

	public CollectionsStatsCountWorker(String uri, String db) {
		this.uri = uri;
		this.database = db;
	}
	
	@Override
	public void run() {

		ConnectionString connectionString = new ConnectionString(uri);
      	
		MongoClient mongoClient = MongoClients.create(connectionString);
		MongoDatabase db = mongoClient.getDatabase(database);
		
		MongoIterable<String> collections = db.listCollectionNames();
		
		MongoCursor<String> cursor = collections.iterator();
		
		try {
		    while (cursor.hasNext()) {
		    	String name = cursor.next();
		    	MongoCollection<Document> collection = db.getCollection(name);
		        System.out.println(collection);
		    }
		} finally {
		    cursor.close();
		}
	}

	
	public static void main(String[] args) {
		
		CollectionsStatsCountWorker count = new CollectionsStatsCountWorker("mongodb://localhost:27017", "test");
		
		count.run();
	}

	
}
