package org.mongodb.sample;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bson.Document;

import com.mongodb.ConnectionString;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class MongoWorker implements Runnable {
	
	private final Integer id;
			
	private String uri;
	
	private String db;
	
	private String coll;
	
	List<MongoClient> conn = new ArrayList<>();

	public MongoWorker(int id, String uri, String db, String coll) {
		super();
		this.id = id;
		this.uri = uri;
		this.db = db;
		this.coll = coll;
	}

	@Override
	public void run() {
		
		Thread.currentThread().setName("iot-loader-" + id);
		
		System.out.println(Thread.currentThread().getName()  + " " + new Date() + " start");
		
		try {
			
			
			MongoCollection<Document> collection  = collection(); 
			
			
			for(long i = 0 ; i < 1000000 ; i ++) {
				
				Document doc = new Document();
				doc.append("deviceId", forID(i));
				doc.append("timestamp", new Date().getTime());
				doc.append("temperature", 30);
				doc.append("content", "XUFEBFGSFJSHYSGDJFKFCFGFJBHKH<JDHBFJJRKFXUFEBFGSFJSHYSGDJFKFCFGFJBHKH<JDHBFJJRKFXUFEBFGSFJSHYSGDJFKFCFGFJBHKH<JDHBFJJRKFXUFEBFGSFJSHYSGDJFKFCFGFJBHKH<JDHBFJJRKFXUFEBFGSFJSHYSGDJFKFCFGFJBHKH<JDHBFJJRKFXUFEBFGSFJSHYSGDJFKFCFGFJBHKH<JDHBFJJRKFXUFEBFGSFJSHYSGDJFKFCFGFJBHKH<JDHBFJJRKFXUFEBFGSFJSHYSGDJFKFCFGFJBHKH<JDHBFJJRKFXUFEBFGSFJSHYSGDJFKFCFGFJBHKH<JDHBFJJRKFXUFEBFGSFJSHYSGDJFKFCFGFJBHKH<JDHBFJJRKF");
				doc.append("code", "SZZZURYSGFJDHFJFJFHFJFJHSDC10010SZZZURYSGFJDHFJFJFHFJFJHSDC10010SZZZURYSGFJDHFJFJFHFJFJHSDC10010SZZZURYSGFJDHFJFJFHFJFJHSDC10010SZZZURYSGFJDHFJFJFHFJFJHSDC10010SZZZURYSGFJDHFJFJFHFJFJHSDC10010SZZZURYSGFJDHFJFJFHFJFJHSDC10010SZZZURYSGFJDHFJFJFHFJFJHSDC10010SZZZURYSGFJDHFJFJFHFJFJHSDC10010SZZZURYSGFJDHFJFJFHFJFJHSDC10010SZZZURYSGFJDHFJFJFHFJFJHSDC10010SZZZURYSGFJDHFJFJFHFJFJHSDC10010SZZZURYSGFJDHFJFJFHFJFJHSDC10010SZZZURYSGFJDHFJFJFHFJFJHSDC10010");
				doc.append("type", "C1001005");
				doc.append("count", 945.4712973582698);
				
				collection.insertOne(doc);
				
				if((i+1) % 50000 == 0) {
					System.out.println(Thread.currentThread().getName()  + " " + new Date() + " insert 50k docs");
				}
			}
			
		} finally {
			conn.forEach(c -> {
	    		c.close();
	    	});
		}
		
		System.out.println(Thread.currentThread().getName()  + " " + new Date() + " exit");
	}
	
	private String forID(long i) {
		String id = String.valueOf(i);
		int count = 8 - id.length();
		String prefix = "";
		for (int item = 0 ; item <  count ; item ++) {
			prefix += "0";
		}
		return prefix + id;
	}

	private MongoCollection<Document> collection() {
		
		ConnectionString connectionString = new ConnectionString(uri);
		
		MongoClient mongoClient = MongoClients.create(connectionString);
		conn.add(mongoClient);
		MongoDatabase database = mongoClient.getDatabase(db);
		MongoCollection<Document> collection = database.getCollection(coll);
		
		return collection;
	}

}
