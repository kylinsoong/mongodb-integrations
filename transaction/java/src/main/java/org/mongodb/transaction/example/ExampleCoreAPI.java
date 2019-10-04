package org.mongodb.transaction.example;

import static com.mongodb.ReadConcern.LOCAL;
import static com.mongodb.ReadPreference.primary;
import static com.mongodb.WriteConcern.MAJORITY;

import org.bson.Document;

import com.mongodb.MongoException;
import com.mongodb.TransactionOptions;
import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;

public class ExampleCoreAPI {

	public static void main(String[] args) {

		final MongoClient client = MongoClients.create("mongodb://localhost:27017");
		
		MongoCollection<Document> coll1 = client.getDatabase("db1").getCollection("foo");
        MongoCollection<Document> coll2 = client.getDatabase("db2").getCollection("bar");
        
        TransactionOptions txnOptions = TransactionOptions.builder()
				.readPreference(primary())
				.readConcern(LOCAL)
				.writeConcern(MAJORITY)
				.build();
        
        try (ClientSession session = client.startSession()) {
        	
        	session.startTransaction(txnOptions);
        	
        	coll1.updateOne(Filters.eq("abc", 0), Updates.set("status", "Inactive"));
        	coll2.updateOne(Filters.eq("xyz", 999), Updates.set("status", "Active"));
        	
        	while(true) {
        		try {
					session.commitTransaction();
					System.out.println("Transaction committed");
					break;
				} catch (MongoException e) {
					if (e.hasErrorLabel(MongoException.UNKNOWN_TRANSACTION_COMMIT_RESULT_LABEL)) {
						System.out.println("UnknownTransactionCommitResult, retrying commit operation ...");
						continue;
					} else {
						System.out.println("Exception during commit ...");
						throw e;
					}
				}
        	}
        } 
	}

}
