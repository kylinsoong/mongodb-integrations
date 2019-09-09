package org.mongodb.sample;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.io.IOUtils;
import org.bson.Document;

import com.mongodb.ConnectionString;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class MongoWorker implements Runnable {
	
	private final Integer id;
	
	private String[] types;
	
	private AtomicLong count;
	
	private String uri;
	
	private String db;
	
	private String coll;
	
	Random r = new Random();
	
	List<MongoClient> conn = new ArrayList<>();

	public MongoWorker(int id, String[] types, AtomicLong count, String uri, String db, String coll) {
		super();
		this.id = id;
		this.types = types;
		this.count = count;
		this.uri = uri;
		this.db = db;
		this.coll = coll;
	}
	
	private String formID() {
		int rnd = r.nextInt(types.length);
		return types[rnd] + ":" +count.getAndIncrement();
	}

	@Override
	public void run() {
		
		Thread.currentThread().setName("bulk-loader-" + id);
		
		System.out.println(Thread.currentThread().getName()  + " " + new Date() + " start");
		
		try {
			Document doc = loadFromTemplete();
			
			MongoCollection<Document> collection  = collection(); 
			
			for(int i = 0 ; i < Long.MAX_VALUE ; i ++) {
				doc.append("_id", formID());
				collection.insertOne(doc);
				if((i+1) % 50000 == 0) {
					System.out.println(Thread.currentThread().getName()  + " " + new Date() + " insert 50k docs");
				}
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			conn.forEach(c -> {
	    		c.close();
	    	});
		}
	}
	
	private MongoCollection<Document> collection() {
		
		ConnectionString connectionString = new ConnectionString(uri);
		
		MongoClient mongoClient = MongoClients.create(connectionString);
		conn.add(mongoClient);
		MongoDatabase database = mongoClient.getDatabase(db);
		MongoCollection<Document> collection = database.getCollection(coll);
		
		return collection;
	}
	
	private Document loadFromTemplete() throws IOException {

		ClassLoader classLoader = App.class.getClassLoader();		
		String live = loadJSONFromClasspath(classLoader, "t.json");	
		Document doc = Document.parse(live);
		return doc;
	}

	private  String loadJSONFromClasspath(ClassLoader classLoader, String name) throws IOException {
		
		try (InputStream inputStream = classLoader.getResourceAsStream(name)) {

            String result = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
            
            return result;
        } catch (IOException e) {
            throw e;
        }
	}

}
