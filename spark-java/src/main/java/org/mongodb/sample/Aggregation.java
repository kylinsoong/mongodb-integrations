package org.mongodb.sample;

import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.SparkSession;
import org.bson.Document;

import com.mongodb.spark.MongoSpark;
import com.mongodb.spark.rdd.api.java.JavaMongoRDD;

import static java.util.Collections.singletonList;
import static org.mongodb.sample.Constants.TEST_DB_URL;

public class Aggregation {

	public static void main(String[] args) {

		SparkSession spark = SparkSession.builder()
				.master("local")
				.appName("MongoSparkConnectorIntro")
				.config("spark.mongodb.input.uri", TEST_DB_URL)
				.config("spark.mongodb.input.collection", "myCollection")
				.getOrCreate();
		
		JavaSparkContext jsc = new JavaSparkContext(spark.sparkContext());
		
		JavaMongoRDD<Document> rdd = MongoSpark.load(jsc);
		
		JavaMongoRDD<Document> aggregatedRdd = rdd.withPipeline(
			      singletonList(Document.parse("{ $match: { test : { $gt : 5 } } }")));
		

		aggregatedRdd.foreach(d -> {
			System.out.println(d.toJson());
		});
		
		jsc.close();
	}

}
