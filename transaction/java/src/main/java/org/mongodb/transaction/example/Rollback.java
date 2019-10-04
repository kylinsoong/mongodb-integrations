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
public class Rollback {

	public static void main(String[] args) {

		final MongoClient client = MongoClients.create("mongodb://localhost:27017");
		final ClientSession session = client.startSession();	
		MongoCollection<Document> account = client.getDatabase("db1").getCollection("account");
		
		Document doc = account.find(session, Filters.eq("_id", "10000")).first();
		System.out.println("Read before start transaction: " + doc.get("Address", Document.class).get("street"));
		
		TransactionOptions txnOptions = TransactionOptions.builder()
				.readPreference(primary())
				.readConcern(LOCAL)
				.writeConcern(MAJORITY)
				.build();
		
		session.startTransaction(txnOptions);
		account.updateOne(session, Filters.eq("_id", "10000"), Updates.set("Address.street", "92th"));
		
		doc = account.find(session, Filters.eq("_id", "10000")).first();
		System.out.println("Read after start transaction and made a change: " + doc.get("Address", Document.class).get("street"));
		
		try {
			account.insertOne(session, new Document("_id", "10000").append("accountType", "credit"));
		} catch (MongoException e) {
			System.out.println(e.getClass().getName() + ": " + e.getMessage());
			session.abortTransaction();
		}
		
		doc = account.find(session, Filters.eq("_id", "10000")).first();
		System.out.println("Read after abord transaction: " + doc.get("Address", Document.class).get("street"));
		
		
		session.close();
	}

}
