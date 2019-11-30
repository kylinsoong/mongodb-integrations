package org.mongodb.sample;

import java.util.ArrayList;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class App {
	
	public static String URI = null;
	public static String DB_NAME = null;
	public static String COLL_NAME = null;
	
	public static String FILE_NAME = null;
	
	
	
	public static void main( String[] args ) throws InterruptedException  {
		
		for(int i = 0 ; i < args.length ; i++) {
			
			if(args[i].equals("--uri") || args[i].equals("-u")) {
				URI = args[++i];
			} else if(args[i].equals("--database") || args[i].equals("-d")) {
				DB_NAME = args[++i];
			} else if(args[i].equals("--collection") || args[i].equals("-c")) {
				COLL_NAME = args[++i];	
			}  
		}
		
		
		if(URI == null || DB_NAME == null || COLL_NAME == null) {
			System.out.println("The following 3 parameter is necessary");
			System.out.println("    -u <URI> -d <DB> -c <COLLECTION> ");
			System.exit(1);
		}
		
		int totalThrads = Runtime.getRuntime().availableProcessors() * 2;
		
		ExecutorService executor = Executors.newFixedThreadPool(totalThrads);
		
		ArrayList<MongoWorker> workers = new ArrayList<>();
		
		for (int i = 0 ; i < totalThrads ; i ++) {
			workers.add(new MongoWorker((100 +i ),  URI, DB_NAME, COLL_NAME));
		}
		
		workers.forEach(r -> {
			executor.execute(r);
		});
		
		executor.isShutdown();
		
		executor.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);

    }
	
	

	
}
