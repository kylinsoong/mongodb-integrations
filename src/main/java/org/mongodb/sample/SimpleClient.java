package org.mongodb.sample;

import org.bson.Document;

import com.mongodb.ConnectionString;
import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class SimpleClient {

	public static void main(String[] args) {
		
		String username = "root";
		String password = "mongo";
		String uri = "mongodb://" + username + ":" + password + "@" + "127.0.0.1:27017/admin";
		
		ConnectionString connectionString = new ConnectionString(uri);
		MongoClient mongoClient = MongoClients.create(connectionString);
		
		MongoDatabase database = mongoClient.getDatabase("sample");
		MongoCollection<Document> collection = database.getCollection("samples");
		ClientSession session = mongoClient.startSession();
		
		session.startTransaction();
		collection.insertOne(session, Document.parse("{name: 'test1'}"));
		collection.insertOne(session, Document.parse("{name: 'test2'}"));
		session.commitTransaction();
		System.out.println(mongoClient);
		
		mongoClient.close();
	}

}
