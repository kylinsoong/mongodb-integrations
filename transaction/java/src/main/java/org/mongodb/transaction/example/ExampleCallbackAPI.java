package org.mongodb.transaction.example;


import com.mongodb.TransactionOptions;
import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.TransactionBody;

import static com.mongodb.WriteConcern.MAJORITY;
import static com.mongodb.ReadConcern.LOCAL;
import static com.mongodb.ReadPreference.primary;

import org.bson.Document;

public class ExampleCallbackAPI {

	public static void main(String[] args) {

		String uri = "mongodb://localhost:27000,localhost:27001,localhost:27002/admin?replicaSet=repl";
		
		final MongoClient client = MongoClients.create(uri);
		
		/**
		 * Create collections. CRUD operations in transactions must be on existing collections.
		 */
		client.getDatabase("mydb1").getCollection("foo").withWriteConcern(MAJORITY).insertOne(new Document("abc", 0));
		client.getDatabase("mydb2").getCollection("bar").withWriteConcern(MAJORITY).insertOne(new Document("xyz", 0));
		
		final ClientSession clientSession = client.startSession();
		
		TransactionOptions txnOptions = TransactionOptions.builder()
				.readPreference(primary())
				.readConcern(LOCAL)
				.writeConcern(MAJORITY)
				.build();
			
		TransactionBody<?> txnBody = new TransactionBody<String>() {

			@Override
			public String execute() {
				
				MongoCollection<Document> coll1 = client.getDatabase("mydb1").getCollection("foo");
		        MongoCollection<Document> coll2 = client.getDatabase("mydb2").getCollection("bar");
		        
		        coll1.insertOne(clientSession, new Document("abc", 1));
		        coll2.insertOne(clientSession, new Document("xyz", 999));
		        
				return "SUCCESS";
			}
		};
		
		try {
			clientSession.withTransaction(txnBody, txnOptions);
		} finally {
			clientSession.close();
		}
			
	}

}
