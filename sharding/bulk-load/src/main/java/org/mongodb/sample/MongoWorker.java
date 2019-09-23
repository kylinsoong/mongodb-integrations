package org.mongodb.sample;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
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
	
	private Boolean isMonotonically;
	
	private String fileName;
	
	private long numPerThreads;
	
	private String shardKey;
	
	Random r = new Random();
	
	List<MongoClient> conn = new ArrayList<>();

	public MongoWorker(int id, String[] types, AtomicLong count, String uri, String db, String coll, Boolean isMonotonically, String fileName, long numPerThreads, String shardKey) {
		super();
		this.id = id;
		this.types = types;
		this.count = count;
		this.uri = uri;
		this.db = db;
		this.coll = coll;
		this.isMonotonically = isMonotonically;
		this.fileName = fileName;
		this.numPerThreads = numPerThreads;
		this.shardKey = shardKey;
	}
	
	private String formID() {
		if(isMonotonically) {
			return String.valueOf(count.getAndIncrement());
		} else {
			int rnd = r.nextInt(types.length);
			return types[rnd] + ":" +count.getAndIncrement();
		}
	}

	@Override
	public void run() {
		
		Thread.currentThread().setName("bulk-loader-" + id);
		
		System.out.println(Thread.currentThread().getName()  + " " + new Date() + " start");
		
		try {
			Document doc = loadFromTemplete();
			
			MongoCollection<Document> collection  = collection(); 
			
			long maxSize = Long.MAX_VALUE;
			if(numPerThreads > 0) {
				maxSize = numPerThreads;
			}
			
			for(long i = count.get() ; i < maxSize ; i ++) {
				
				doc.remove("_id");
				
				if(this.shardKey != null && this.shardKey.length() > 0) {
					doc.append(shardKey, formID());
				} else {
					doc.append("_id", formID());
				}
				
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
		
		System.out.println(Thread.currentThread().getName()  + " " + new Date() + " exit");
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
		
		if(fileName != null && Files.exists(Paths.get(fileName))) {
			try (InputStream inputStream = Files.newInputStream(Paths.get(fileName))){
				String docString = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
				Document doc = Document.parse(docString);
				return doc;
			}
		}
		
		String classpathFileName =  "t.json";
		
		if(fileName != null && fileName.length() > 0) {
			classpathFileName = this.fileName;
		}

		ClassLoader classLoader = App.class.getClassLoader();		
		String docString = loadJSONFromClasspath(classLoader, classpathFileName);	
		Document doc = Document.parse(docString);
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
