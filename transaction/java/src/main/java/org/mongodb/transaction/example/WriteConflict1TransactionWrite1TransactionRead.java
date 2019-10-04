package org.mongodb.transaction.example;

import static com.mongodb.ReadConcern.LOCAL;
import static com.mongodb.ReadPreference.primary;
import static com.mongodb.WriteConcern.MAJORITY;

import org.bson.Document;

import com.mongodb.TransactionOptions;
import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;

/**
 * 
 *  Insert on records in database 'db1'
 *  
 *     use db1
 *     db.account.insertOne({_id: "10000", name: { first: "Kylin", last: "Soong"}, accountType: "credit", contact: { phoneNumber: 18611907049},Address: { city: "Beijing", street: "94th", zipecode: 100006}})
 *     db.account.find()
 *     
 * @author ksoong
 *
 */
public class WriteConflict1TransactionWrite1TransactionRead {

	public static void main(String[] args) {

		final MongoClient c1 = MongoClients.create("mongodb://localhost:27017");
		final ClientSession s1 = c1.startSession();	
		MongoCollection<Document> s1account = c1.getDatabase("db1").getCollection("account");
		
		TransactionOptions txnOptions_1 = TransactionOptions.builder()
				.readPreference(primary())
				.readConcern(LOCAL)
				.writeConcern(MAJORITY)
				.build();
		
		final MongoClient c2 = MongoClients.create("mongodb://localhost:27017");
		final ClientSession s2 = c2.startSession();
		MongoCollection<Document> s2account = c2.getDatabase("db1").getCollection("account");
		
		TransactionOptions txnOptions_2 = TransactionOptions.builder()
				.readPreference(primary())
				.readConcern(LOCAL)
				.writeConcern(MAJORITY)
				.build();
		
		s1.startTransaction(txnOptions_1);
		
		Document account = s1account.find(s1, Filters.eq("Address.street", "94th")).first();
		System.out.println("Read(session 1): " + account.get("Address", Document.class).get("street"));
		account = s2account.find(s2, Filters.eq("_id", "10000")).first();
		System.out.println("Read(session 2): " + account.get("Address", Document.class).get("street"));
		
		
		s2.startTransaction(txnOptions_2);
		s2account.updateOne(s2, Filters.eq("_id", "10000"), Updates.set("Address.street", "95th"));
		
		account = s1account.find(s1, Filters.eq("_id", "10000")).first();
		System.out.println("Read(session 1): " + account.get("Address", Document.class).get("street"));
		account = s2account.find(s2, Filters.eq("_id", "10000")).first();
		System.out.println("Read(session 2): " + account.get("Address", Document.class).get("street"));
		
		s1.commitTransaction();
		s2.commitTransaction();
		
		account = s1account.find(s1, Filters.eq("_id", "10000")).first();
		System.out.println("Read(session 1): " + account.get("Address", Document.class).get("street"));
		account = s2account.find(s2, Filters.eq("_id", "10000")).first();
		System.out.println("Read(session 2): " + account.get("Address", Document.class).get("street"));
		
		s1.close();
		s2.close();
			
	}

}
