package org.mongodb.perf;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.bson.BsonNull;
import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.ConnectionString;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

import static com.mongodb.client.model.Aggregates.*;
import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Accumulators.*;
import static com.mongodb.client.model.Sorts.*;
import static com.mongodb.client.model.Projections.*;

public class AggregationPerformanceWorker implements Worker {
	
	private final Integer id;
	
	private String uri;
	
	private String db;
	
	private String coll;
	
	private Boolean isPrintResult = false;

	public AggregationPerformanceWorker(Integer id, String uri, String db, String coll, Boolean isPrintResult ) {
		super();
		this.id = id;
		this.uri = uri;
		this.db = db;
		this.coll = coll;
		this.isPrintResult = isPrintResult;
	}
	
	@Override
	public void run() {
		
		Thread.currentThread().setName("aggregation-" + id);
		
        ConnectionString connectionString = new ConnectionString(uri);
        
//        MongoClientSettings settings = MongoClientSettings.builder().applyConnectionString(connectionString).build();
        	
		MongoClient mongoClient = MongoClients.create(connectionString);
		MongoDatabase database = mongoClient.getDatabase(db);
		MongoCollection<Document> collection = database.getCollection(coll);
		
		List<? extends Bson> pipeline = Arrays.asList(
				match(or(Arrays.asList(
						ne("ibkOrg", "00001"), 
						eq("ibkOrg", new BsonNull()), 
						ne("ibkName", "总行（本部）              "),
						eq("ibkName", new BsonNull())))), 
				group(and(eq("ibkOrg", "$ibkOrg"), eq("ibkName", "$ibkName")), sum("Sum_交易金额折人民币", "$traCny"), max("Sum_交易金额折人民币_max0", "$traCny")), 
				project(fields(excludeId(), 
						computed("ibkOrg", "$_id.ibkOrg"), 
						computed("ibkName", "$_id.ibkName"), 
						computed("Sum_交易金额折人民币", 
								eq("$cond", and(eq("if", Arrays.asList("$Sum_交易金额折人民币_max0", new BsonNull())), 
										eq("then", new BsonNull()), 
										eq("else", "$Sum_交易金额折人民币")))))), 
				sort(orderBy(ascending("ibkOrg"), ascending("ibkName"))), 
				limit(5000000));
		
		Date start = new Date() ;
		System.out.println(Thread.currentThread().getName()  + " start (" + start + ")");
		
		AggregateIterable<Document> results = collection.aggregate(pipeline);
		
		if(isPrintResult) {
			if(isPrintResult) {
				MongoCursor<Document> cursor = results.iterator();
				try {
				    while (cursor.hasNext()) {
				        System.out.println(cursor.next().toJson());
				    }
				} finally {
				    cursor.close();
				}
			}
		} else {
			MongoCursor<Document> cursor = results.iterator();
			try {
			    while (cursor.hasNext()) {
			    	cursor.next();
			    }
			} finally {
			    cursor.close();
			}
		}
        
		Date end = new Date() ;
		System.out.println(Thread.currentThread().getName()  + "  exit (" + end + "), total spend(milliseconds) " + (end.getTime() - start.getTime()));
	
		mongoClient.close();
	}
	
}
