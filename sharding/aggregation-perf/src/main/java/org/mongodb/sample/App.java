package org.mongodb.sample;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


public class App {
	
	//?readPreference=secondaryPreferred
	
	public static String URI = "mongodb://localhost:27017";
	public static String DB_NAME = "TBDSS_TBDSSBI";
	public static String COLL_NAME = "ApcCdeTraCollection";
	public static Integer THREADS  = 2;
	public static Boolean IS_PRINT  = false;
	
	
	
	public static void main( String[] args ) throws InterruptedException, FileNotFoundException, IOException  {
		
		String confName = null;
		
		for(int i = 0 ; i < args.length ; i++) {
			if(args[i].equals("--config") || args[i].equals("-conf")) {
				confName = args[++i];
			}
		}
		
		initFromConf(confName);
		
		
		for(int i = 0 ; i < args.length ; i++) {
			
			if(args[i].equals("--uri") || args[i].equals("-u")) {
				URI = args[++i];
			} else if(args[i].equals("--database") || args[i].equals("-d")) {
				DB_NAME = args[++i];
			} else if(args[i].equals("--collection") || args[i].equals("-c")) {
				COLL_NAME = args[++i];	
			} else if(args[i].equals("--threads") || args[i].equals("-t")) {
				THREADS = Integer.parseInt(args[++i]);	
			} else if(args[i].equals("--print") || args[i].equals("-p")) {
				IS_PRINT = Boolean.valueOf(true);	
			}  
		}
		
		System.out.println("Connection URI for MongoDB: " + URI);
		System.out.println("Database Name: " + DB_NAME);
		System.out.println("Collection Name: " + COLL_NAME);
		System.out.println("Current Load Number: " + THREADS);
		System.out.println("Whether Print Result: " + IS_PRINT);
		
		ExecutorService executor = Executors.newFixedThreadPool(THREADS);
		
		ArrayList<MongoWorker> workers = new ArrayList<>();
		
		for (int i = 0 ; i < THREADS ; i ++) {
			workers.add(new MongoWorker(i, URI, DB_NAME, COLL_NAME, IS_PRINT));
		}
		
		workers.forEach(r -> {
			executor.execute(r);
		});

		executor.isShutdown();

		executor.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
		

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
