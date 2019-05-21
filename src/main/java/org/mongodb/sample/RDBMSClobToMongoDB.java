package org.mongodb.sample;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.bson.BsonValue;
import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.Block;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import com.mongodb.client.gridfs.GridFSFindIterable;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.mongodb.client.gridfs.model.GridFSUploadOptions;
import com.mongodb.client.model.Filters;

public class RDBMSClobToMongoDB implements AutoCloseable{
	
	private static final String USERNAME = "root";
	private static final String PASSWORD = "mongo";
	private static final String URL = "mongodb://" + USERNAME + ":" + PASSWORD + "@" + "localhost:27000,localhost:27001,localhost:27002/admin";
	
	private static MongoClient client = null;
	
	static InputStream open(String fileName) throws FileNotFoundException {
		File file = Paths.get("/Users/ksoong/tmp", fileName).toFile();
		return new FileInputStream(file);
	}
	
	static Object[] sample() {
		
		Object[] array = {100001, "name", "type", "date", "desc", "clob1.xml", "clob2.xml", "clob3.xml", "clob4.xml", "clob5.xml"};
		
		return array;
	}
	
	protected MongoClient mongoClientInstance() {
		
		if(null != client) {
			return client;
		}
				
		ConnectionString connectionString = new ConnectionString(URL);
	    MongoClientSettings clientSettings =
	        MongoClientSettings.builder()
	            .applyConnectionString(connectionString)
	            .applicationName(RDBMSClobToMongoDB.class.getSimpleName())
	            .applyToConnectionPoolSettings(
	                builder -> builder.maxWaitTime(1000, TimeUnit.MILLISECONDS))
	            .build();
	    
	    client = MongoClients.create(clientSettings);
		
		return client;
	}
	
	protected MongoDatabase mongoDatabaseInstance(String dbName) {
	    
	    MongoClient mongoClient = mongoClientInstance();
	    
	    MongoDatabase database = mongoClient.getDatabase(dbName);
	    
	    return database;
	}
	

	public static void main(String[] args) throws Exception {

//		uoload();
		
		download();
	}
	
	static void uoload() throws Exception {

		RDBMSClobToMongoDB sample = new RDBMSClobToMongoDB();
		
		MongoDatabase database = sample.mongoDatabaseInstance("brms");
		
		GridFSBucket gridFSBucket = GridFSBuckets.create(database, "tableA");
		MongoCollection<Document> collection = database.getCollection("tableA");
		
		Object[] tuple = sample();
		
		collection.insertOne(formatDocuemnt(tuple));
		System.out.println("Insert a document");
		
		Integer id = (Integer) tuple[0];
		
		String clob1 = (String) tuple[5];
		GridFSUploadOptions clob1Options = new GridFSUploadOptions().metadata(formatDocuemnt(id, clob1));
		ObjectId fileId1 = gridFSBucket.uploadFromStream(clob1, open(clob1), clob1Options);
		System.out.println("Insert " + clob1 + ", " + fileId1);
		
		String clob2 = (String) tuple[6];
		GridFSUploadOptions clob2Options = new GridFSUploadOptions().metadata(formatDocuemnt(id, clob2));
		ObjectId fileId2 = gridFSBucket.uploadFromStream(clob2, open(clob2), clob2Options);
		System.out.println("Insert " + clob2 + ", " + fileId2);
		
		String clob3 = (String) tuple[7];
		GridFSUploadOptions clob3Options = new GridFSUploadOptions().metadata(formatDocuemnt(id, clob3));
		ObjectId fileId3 = gridFSBucket.uploadFromStream(clob3, open(clob3), clob3Options);
		System.out.println("Insert " + clob3 + ", " + fileId3);
		
		String clob4 = (String) tuple[8];
		GridFSUploadOptions clob4Options = new GridFSUploadOptions().metadata(formatDocuemnt(id, clob4));
		ObjectId fileId4 = gridFSBucket.uploadFromStream(clob4, open(clob4), clob4Options);
		System.out.println("Insert " + clob4 + ", " + fileId4);
		
		String clob5 = (String) tuple[9];
		GridFSUploadOptions clob5Options = new GridFSUploadOptions().metadata(formatDocuemnt(id, clob5));
		ObjectId fileId5 = gridFSBucket.uploadFromStream(clob5, open(clob5), clob5Options);
		System.out.println("Insert " + clob5 + ", " + fileId5);
		
		sample.close();
	}

	static void download() throws Exception {

		RDBMSClobToMongoDB sample = new RDBMSClobToMongoDB();
		
		MongoDatabase database = sample.mongoDatabaseInstance("brms");
		
		GridFSBucket gridFSBucket = GridFSBuckets.create(database, "tableA");
		
		MongoCursor<GridFSFile> it = gridFSBucket.find(Filters.eq("metadata.id", 100001)).iterator();
		while(it.hasNext()) {
			GridFSFile gridFSFile = it.next();
			String filename = gridFSFile.getFilename();
			BsonValue value = gridFSFile.getId();
			FileOutputStream streamToDownloadTo = new FileOutputStream("target/" + filename);
			gridFSBucket.downloadToStream(value, streamToDownloadTo);
			System.out.println("downloading " + filename + " - " + value);
			streamToDownloadTo.flush();
			streamToDownloadTo.close();
		}
		
		
		sample.close();
	}

	private static Document formatDocuemnt(Object[] tuple) {
		Map<String, Object> map = new HashMap<>();
		map.put("id", tuple[0]);
		map.put("name", tuple[1]);
		map.put("type", tuple[2]);
		map.put("date", tuple[3]);
		map.put("desc", tuple[4]);
		map.put("clob1", tuple[5]);
		map.put("clob2", tuple[6]);
		map.put("clob3", tuple[7]);
		map.put("clob4", tuple[8]);
		map.put("clob5", tuple[9]);
		return new Document(map);
	}

	static Document formatDocuemnt(Object id, Object clobName) {
		Map<String, Object> map = new HashMap<>();
		map.put("id", id);
		map.put("clobName", clobName);
		return new Document(map);
	}
	

	@Override
	public void close() throws Exception {

		if(null != client) {
			client.close();
		}
	}

}
