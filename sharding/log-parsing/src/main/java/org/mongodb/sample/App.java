package org.mongodb.sample;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import org.bson.Document;

import com.mongodb.ConnectionString;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class App {
	
	public static String URI = "mongodb://localhost:27017";
	public static String DB_NAME = "CBIT";
	public static String COLL_NAME = "LOG";
	
	public static String FILE_NAME = "/Users/ksoong/tmp/cbit/log/4/dzbd-debug.2019-10-09.log";
	
	public static String APP_NAME = "保单生成";
	
	
	public static void main( String[] args ) throws InterruptedException  {
		
		for(int i = 0 ; i < args.length ; i++) {
			
			if(args[i].equals("--uri") || args[i].equals("-u")) {
				URI = args[++i];
			} else if(args[i].equals("--database") || args[i].equals("-d")) {
				DB_NAME = args[++i];
			} else if(args[i].equals("--collection") || args[i].equals("-c")) {
				COLL_NAME = args[++i];	
			}  else if(args[i].equals("--file") || args[i].equals("-f")) {
				FILE_NAME = args[++i];	
			} else if(args[i].equals("--app") || args[i].equals("-a")) {
				APP_NAME = args[++i];	
			} 
		}
		
				
		File file = new File(FILE_NAME);
		
		ConnectionString connectionString = new ConnectionString(URI);
		
		MongoClient mongoClient = MongoClients.create(connectionString);
		MongoDatabase database = mongoClient.getDatabase(DB_NAME);
		MongoCollection<Document> collection = database.getCollection(COLL_NAME);
		
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
		    String line;
		    while ((line = br.readLine()) != null) {
		    	
		    	Document doc = new Document();
				doc.append("app", APP_NAME);
		    	
		    	String[] tokens = line.split("\\]-\\[");
		    	
		    	if(tokens.length == 4) {
		    		String date = trim(tokens[0]);
		    		String thread = trim(tokens[1]);
		    		String level = trim(tokens[2]);
		    		String content = trim(tokens[3]);
		    		doc.append("date", date);
		    		doc.append("thread", thread);
		    		doc.append("level", level);
		    		doc.append("log", content);
		    		collection.insertOne(doc);
		    	}
		    	
//		    	System.out.println(tokens.length);
//		    	System.out.println();
		    }
		} catch (Exception e) {
			throw new RuntimeException(e);
		} 
		
		mongoClient.close();

    }

	private static String trim(String content) {
		
		if(content.startsWith("[")) {
			content = content.substring(1);
		}
		
		if(content.endsWith("]")) {
			content = content.substring(0, content.length() -1);
		}
			
		return content;
	}
	
}
