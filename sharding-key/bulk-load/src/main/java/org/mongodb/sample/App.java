package org.mongodb.sample;


import java.util.ArrayList;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;


public class App {
	
	public static String URI = null;
	public static String DB_NAME = null;
	public static String COLL_NAME = null;
	
	static String[] TYPES = new String[] {"DEBIT_CARD_CHN", "CREDIT_CARD_CHN", "TEST_ABC_DEF", "TEST_BCD_DEF", "TEST_YHN_OSD", "TEST_GHD_YCD", "TEST_IJN_POI"};
	static AtomicLong ID = new AtomicLong(10000000000000000L);
	
	public static void main( String[] args ) throws InterruptedException  {
		
		for(int i = 0 ; i < args.length ; i++) {
			
			if(args[i].equals("-uri")) {
				URI = args[++i];
			} else if(args[i].equals("-d")) {
				DB_NAME = args[++i];
			} else if(args[i].equals("-c")) {
				COLL_NAME = args[++i];	
			}
		}
		
		if(URI == null || DB_NAME == null || COLL_NAME == null) {
			System.out.println("The following 3 parameter is necessary");
			System.out.println("    -uri <URI> -d <DB> -c <COLLECTION>");
			System.exit(1);
		}
		
		
		int totalThrads = Runtime.getRuntime().availableProcessors() * 2;
		
		ExecutorService executor = Executors.newFixedThreadPool(totalThrads);
		
		ArrayList<MongoWorker> workers = new ArrayList<>();
		
		for (int i = 0 ; i < totalThrads ; i ++) {
			workers.add(new MongoWorker((100 +i ), TYPES, ID, URI, DB_NAME, COLL_NAME));
		}
		
		workers.forEach(r -> {
			executor.execute(r);
		});
		
		executor.isShutdown();
		
		executor.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
		
    }
	
	

	
}
