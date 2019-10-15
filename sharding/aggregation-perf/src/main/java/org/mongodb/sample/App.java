package org.mongodb.sample;


import java.util.ArrayList;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


public class App {
	
	public static String URI = "mongodb://tbdssBI:Gcms201901!@22.188.70.193:32000/test?authSource=TBDSS_TBDSSBI";
	public static String DB_NAME = "TBDSS_TBDSSBI";
	public static String COLL_NAME = "ApcCdeTraCollection";
	public static Integer THREADS  = 2;
	public static Boolean IS_PRINT  = false;
	
	
	
	public static void main( String[] args ) throws InterruptedException  {
		
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
	
	

	
}
