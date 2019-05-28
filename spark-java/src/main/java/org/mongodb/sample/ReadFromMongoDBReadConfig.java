package org.mongodb.sample;

import java.util.HashMap;
import java.util.Map;

import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.SparkSession;
import org.bson.Document;

import com.mongodb.spark.MongoSpark;
import com.mongodb.spark.config.ReadConfig;
import com.mongodb.spark.rdd.api.java.JavaMongoRDD;

import static org.mongodb.sample.Constants.TEST_DB_URL;

public class ReadFromMongoDBReadConfig {

	public static void main(String[] args) {

		SparkSession spark = SparkSession.builder()
				.master("local")
				.appName("MongoSparkConnectorIntro")
				.config("spark.mongodb.input.uri", TEST_DB_URL)
				.config("spark.mongodb.output.uri", TEST_DB_URL)
				.config("spark.mongodb.input.collection", "myCollection")
				.getOrCreate();
		
		JavaSparkContext jsc = new JavaSparkContext(spark.sparkContext());
		
		Map<String, String> readOverrides = new HashMap<String, String>();
	    readOverrides.put("collection", "spark");
	    readOverrides.put("readPreference.name", "secondaryPreferred");
	    ReadConfig readConfig = ReadConfig.create(jsc).withOptions(readOverrides);
		
		JavaMongoRDD<Document> rdd = MongoSpark.load(jsc, readConfig);
		
		System.out.println(rdd.count());
		
		rdd.foreach(d -> {
			System.out.println(d.toJson());
		});
		
		jsc.close();
	}

}
