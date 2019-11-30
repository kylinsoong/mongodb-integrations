package org.mongodb.sample;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.IOUtils;
import org.bson.Document;

import com.mongodb.ConnectionString;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.InsertOneModel;
import com.mongodb.client.model.WriteModel;

public class SimpleLoader {
	
	static {
		Logger mongoLogger = Logger.getLogger( "org.mongodb.driver" );
		mongoLogger.setLevel(Level.SEVERE);
	}
	
	public static String URI = "mongodb://localhost:27017";
	public static String DB_NAME = "pomp";
	public static String COLL_NAME = "bulkWrite";
	
	
	private static String fileName = "pomp.json";

	public static void main(String[] args) throws IOException, InterruptedException {

		
		for(int i = 0 ; i < 100 ; i ++) {
			start();
			Thread.sleep(1000 * 60);
		}
		
		Thread.currentThread().sleep(1000 * 60);
		
	}
	
	

	private static void start() {

		for(int i = 0 ; i < 10 ; i ++) {
			new Thread(new Runnable() {

				String prefix = UUID.randomUUID().toString().substring(0, 8);
				
				@Override
				public void run() {
					
					ConnectionString connectionString = new ConnectionString(URI);
					
					MongoClient mongoClient = MongoClients.create(connectionString);
					MongoDatabase database = mongoClient.getDatabase(DB_NAME);
					MongoCollection<Document> collection = database.getCollection(COLL_NAME + "_" + prefix);
					
					List<? extends WriteModel<? extends Document>> requests;
					try {
						requests = loadRequests();
						collection.bulkWrite(requests);
					} catch (IOException e) {
						e.printStackTrace();
					}
					
					mongoClient.close();
					
					System.out.println(prefix + " bulk write 10k docs");
					
				}}).start();
		}
	}



	private static List<? extends WriteModel<? extends Document>> loadRequests() throws IOException {
		
		List<InsertOneModel<Document>> lists = new ArrayList<>();
		
		String prefix = UUID.randomUUID().toString().substring(0, 8);
		
		for(int i = 0 ; i < 10000 ; i ++) {
			Document doc = loadFromTemplete();
			String uuid = prefix + "-" + i;
			doc.append("uuid", uuid);
			lists.add(new InsertOneModel<>(doc));
		}
		
		
		return lists;
	}



	static Document loadFromTemplete() throws IOException {
		
		if(fileName != null && Files.exists(Paths.get(fileName))) {
			try (InputStream inputStream = Files.newInputStream(Paths.get(fileName))){
				String docString = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
				Document doc = Document.parse(docString);
				return doc;
			}
		}
		
		String classpathFileName =  "t.json";
		
		if(fileName != null && fileName.length() > 0) {
			classpathFileName = fileName;
		}

		ClassLoader classLoader = App.class.getClassLoader();		
		String docString = loadJSONFromClasspath(classLoader, classpathFileName);	
		Document doc = Document.parse(docString);
		return doc;
	}

	static  String loadJSONFromClasspath(ClassLoader classLoader, String name) throws IOException {
		
		try (InputStream inputStream = classLoader.getResourceAsStream(name)) {

            String result = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
            
            return result;
        } catch (IOException e) {
            throw e;
        }
	}

}
