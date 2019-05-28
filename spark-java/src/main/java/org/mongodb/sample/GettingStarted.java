package org.mongodb.sample;

import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.SparkSession;

import static org.mongodb.sample.Constants.TEST_DB_URL;

public class GettingStarted {

	public static void main(String[] args) {
		
		SparkSession spark = SparkSession.builder()
				.master("local")
				.appName("MongoSparkConnectorIntro")
				.config("spark.mongodb.input.uri", TEST_DB_URL)
				.config("spark.mongodb.output.uri", TEST_DB_URL)
				.config("spark.mongodb.input.collection", "myCollection")
				.getOrCreate();
		
		JavaSparkContext jsc = new JavaSparkContext(spark.sparkContext());
				

		System.out.println(jsc);
		
		jsc.close();
	}

}
