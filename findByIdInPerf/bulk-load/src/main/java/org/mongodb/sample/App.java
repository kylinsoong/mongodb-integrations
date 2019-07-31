package org.mongodb.sample;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

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
		
		loadTemplete();
    	
		new Thread(new Runnable() {

			@Override
			public void run() {

				MongoCollection<Document> collection = collection();
				
				long start = System.currentTimeMillis();
				for(int i = 0 ; i < 5000000 ; i ++) {
					String id = i + "" ;
					liveDoc.append("_id", id);
					collection.insertOne(liveDoc);				
				}
				
				System.out.println(Thread.currentThread().getName() + " insert 5000000 live doc" + " spend " + (System.currentTimeMillis() - start) + " mulliseconds");
			}}).start();
		
		new Thread(new Runnable() {

			@Override
			public void run() {

				MongoCollection<Document> collection = collection();
				long start = System.currentTimeMillis();
				for(int i = 5000000 ; i < 10000000 ; i ++) {
					String id = i + "" ;
					movieDoc.append("_id", id);
					collection.insertOne(movieDoc);
				}
				System.out.println(Thread.currentThread().getName() + " insert 5000000 movie doc" + " spend " + (System.currentTimeMillis() - start) + " mulliseconds");
			}}).start();
		
		new Thread(new Runnable() {

			@Override
			public void run() {

				MongoCollection<Document> collection = collection();
				long start = System.currentTimeMillis();
				for(int i = 10000000 ; i < 15000000 ; i ++) {
					String id = i + "" ;
					tiktokDoc.append("_id", id);
					collection.insertOne(tiktokDoc);
				}
				
				System.out.println(Thread.currentThread().getName() + " insert 5000000 tiktok doc" + " spend " + (System.currentTimeMillis() - start) + " mulliseconds");
				
			}}).start();
		
		new Thread(new Runnable() {

			@Override
			public void run() {

				MongoCollection<Document> collection = collection();
				long start = System.currentTimeMillis();
				for(int i = 15000000 ; i < 20000000 ; i ++) {
					String id = i + "" ;
					tvDoc.append("_id", id);
					collection.insertOne(tvDoc);
				}
				
				System.out.println(Thread.currentThread().getName() + " insert 5000000 tv doc" + " spend " + (System.currentTimeMillis() - start) + " mulliseconds");
				
			}}).start();
		
		System.out.println("\n\n\tWait work thread to ending...\n\n");
		
		Thread.sleep(Long.MAX_VALUE);
				
		destroy();
    }
	
	private static void loadTemplete() throws IOException {

		ClassLoader classLoader = App.class.getClassLoader();
		
		String live = loadJSONFromClasspath(classLoader, "live.json");
		String movie = loadJSONFromClasspath(classLoader, "movie.json");
		String tiktok = loadJSONFromClasspath(classLoader, "tiktok.json");
		String tv = loadJSONFromClasspath(classLoader, "tv.json");
		
		liveDoc = Document.parse(live);
		movieDoc = Document.parse(movie);
		tiktokDoc = Document.parse(tiktok);
		tvDoc = Document.parse(tv);
	}

	private static String loadJSONFromClasspath(ClassLoader classLoader, String name) throws IOException {
		
		try (InputStream inputStream = classLoader.getResourceAsStream(name)) {

            String result = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
            
            return result;
        } catch (IOException e) {
            throw e;
        }
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
