package org.mongodb.perf;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.ConnectionString;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;


public class App {
	
	//?readPreference=secondaryPreferred
	
	public static String URI = "mongodb://localhost:27017";
	public static String DB_NAME = "test";
	public static String COLL_NAME = "ApcCdeTraCollection";
	public static Integer THREADS  = 2;
	public static Boolean IS_PRINT  = false;
	
	public static Boolean IS_COUNT = false;
	
	
	public static void main( String[] args ) throws InterruptedException, FileNotFoundException, IOException  {
		
		String confName = null;
		String pipelinesFile = null;
		
		for(int i = 0 ; i < args.length ; i++) {
			if(args[i].equals("--config") || args[i].equals("-conf")) {
				confName = args[++i];
			} else if(args[i].equals("--pipeline") || args[i].equals("-pipeline")) {
				pipelinesFile = args[++i];
			}
		}
		
		initFromConf(confName);
		
		
		for(int i = 0 ; i < args.length ; i++) {
			
			if(args[i].equals("--uri") || args[i].equals("-uri")) {
				URI = args[++i];
			} else if(args[i].equals("--database") || args[i].equals("-d")) {
				DB_NAME = args[++i];
			} else if(args[i].equals("--collection") || args[i].equals("-c")) {
				COLL_NAME = args[++i];	
			} else if(args[i].equals("--threads") || args[i].equals("-t")) {
				THREADS = Integer.parseInt(args[++i]);	
			} else if(args[i].equals("--print") || args[i].equals("-p")) {
				IS_PRINT = Boolean.valueOf(true);	
			}  else if(args[i].equals("--count") || args[i].equals("-count")) {
				IS_COUNT = Boolean.valueOf(true);	
			}  
		}
		
		System.out.println("Connection URI for MongoDB: " + URI);
		System.out.println("Database Name: " + DB_NAME);
		
		
		if(IS_COUNT) { 
			CollectionsStatsCountWorker count = new CollectionsStatsCountWorker(URI, DB_NAME);
			System.exit(0);
		}
		
		System.out.println("Collection Name: " + COLL_NAME);
		System.out.println("Current Load Number: " + THREADS);
		System.out.println("Whether Print Result: " + IS_PRINT);
		
		if(pipelinesFile != null) {
			
			Pipelines p = new Pipelines(pipelinesFile);
			
			ConnectionString connectionString = new ConnectionString(URI);
			MongoClient mongoClient = MongoClients.create(connectionString);
			MongoDatabase database = mongoClient.getDatabase(DB_NAME);
			MongoCollection<Document> collection = database.getCollection(COLL_NAME);
			
			for(int i = 0 ; i < p.pipelines.size() ; i ++) {
				
				System.out.println("$ Aggregation Pipeline [" + (i + 1) + "]");
				System.out.println("$ 优化前：");
				System.out.println(p.original(i));
				
				System.out.println("$ 优化后：");
				System.out.println(p.optimized(i));
				
				List<? extends Bson> pipeline = p.pipeline(i);
				
				Date start = new Date() ;
				Date computeEnd = null;
				AggregateIterable<Document> results = collection.aggregate(pipeline);
				MongoCursor<Document> cursor = results.iterator();
				
				boolean lighter = true;
				while (cursor.hasNext()) {
					cursor.next().toJson();
					if(lighter) {
						lighter = false;
						computeEnd = new Date();
					}
				}
				lighter = true;
				
				Date end = new Date() ;
				if(computeEnd == null) {
					computeEnd = end;
				}
				
				System.out.println("$ 时间统计：\n总时间：" + (end.getTime() - start.getTime()) + "毫秒， 数据查询时间：" + (computeEnd.getTime() - start.getTime()) + "毫秒， 遍历及反序列化时间：" + (end.getTime() - computeEnd.getTime() + "毫秒\n\n"));
			}
			
		} else {
			ExecutorService executor = Executors.newFixedThreadPool(THREADS);
			
			ArrayList<AggregationPerformanceWorker> workers = new ArrayList<>();
			
			for (int i = 0 ; i < THREADS ; i ++) {
				workers.add(new AggregationPerformanceWorker(i, URI, DB_NAME, COLL_NAME, IS_PRINT));
			}
			
			workers.forEach(r -> {
				executor.execute(r);
			});

			executor.isShutdown();

			executor.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
		}
		
    }



	private static void initFromConf(String confName) throws FileNotFoundException, IOException {
		
		if(null == confName) {
			return ;
		}

		Properties prop = new Properties();

		prop.load(new FileInputStream(confName));
		
		URI = prop.getProperty("tbdssbi.connection.uri", URI);
		DB_NAME = prop.getProperty("tbdssbi.database.name", DB_NAME);
		COLL_NAME = prop.getProperty("tbdssbi.collection.name", COLL_NAME);
		THREADS = Integer.parseInt(prop.getProperty("tbdssbi.load.current", String.valueOf(THREADS)));
		IS_PRINT = Boolean.parseBoolean(prop.getProperty("tbdssbi.load.print", "false"));
	}
	
	

	
}
