package org.mongodb.sample;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.bson.Document;
import org.bson.conversions.Bson;


import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

public class App {
	
	public static final String HOSTNAME = "localhost";
	public static final String DB_NAME = "migu";
	public static final String COLL_NAME = "videos";
	
	public static final Integer PORT = 27017;
	
	static List<MongoClient> conn = new ArrayList<>();
	
	static Document liveDoc = null;
	static Document movieDoc = null;
	static Document tiktokDoc = null;
	static Document tvDoc = null;

	public static void main( String[] args ) throws IOException, InterruptedException {
		
		Integer count = 100;
		
		if(args.length == 1) {
			count = Integer.parseInt(args[0]);
		}
		
		
		System.out.println("\n\n\t $in performance test(Id array length is ) + " + count + "\n\n");
		
		List<String> ids = generateRandomId(count);
		
		long[] queryArray = new long[10];
		long[] iteratorArray = new long[10];
		
		for (int i = 0 ; i < 10 ; i++) {
			 MongoCollection<Document> collection = collection();
		        
		        Bson query = Filters.in("_id", ids.toArray());
		        long start = System.currentTimeMillis();
		        MongoCursor<Document> cursor = collection.find(query).iterator();
		        long queryCost = System.currentTimeMillis() - start;
		        queryArray[i] = queryCost;
		        while (cursor.hasNext()) {
		        	cursor.next().toJson();
		        }
				
		        long iteratorCost = System.currentTimeMillis() - start;
		        iteratorArray[i] = iteratorCost;
		        System.out.println("\n" + (i + 1) + " - "+ " Query spend " + queryCost + " milliseconds, Iterator spend " + iteratorCost + " milliseconds\n");
						
				destroy();
		}
		
		System.out.println("\n\n\t Average Query spend " + countAvg(queryArray) + " milliseconds, Iterator spend " + countAvg(iteratorArray) + "\n\n");
		
    }
	
	


	private static long countAvg(long[] array) {
		return Arrays.stream(array).sum()/array.length;
	}




	private static List<String> generateRandomId(Integer count) {
		List<String> ids = new ArrayList<>();
		
		for (int i = 0 ; i < count ; i ++) {
			int randomNum = ThreadLocalRandom.current().nextInt(0, 20000000);
			ids.add(randomNum + "");
		}
		
		return ids;
	}




	static MongoCollection<Document> collection() {
		
		MongoClient mongoClient = new MongoClient(HOSTNAME, PORT);
		conn.add(mongoClient);
		MongoDatabase database = mongoClient.getDatabase(DB_NAME);
		MongoCollection<Document> collection = database.getCollection(COLL_NAME);
		
		return collection;
	}
    
    static void destroy() {
    	conn.forEach(c -> {
    		c.close();
    	});
    }
}
