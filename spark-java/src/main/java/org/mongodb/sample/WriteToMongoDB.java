package org.mongodb.sample;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.sql.SparkSession;
import org.bson.Document;

import com.mongodb.spark.MongoSpark;

import static java.util.Arrays.asList;
import static org.mongodb.sample.Constants.TEST_DB_URL;

public class WriteToMongoDB {

	public static void main(String[] args) {

		SparkSession spark = SparkSession.builder()
				.master("local")
				.appName("MongoSparkConnectorIntro")
				.config("spark.mongodb.output.uri", TEST_DB_URL)
				.config("spark.mongodb.output.collection", "myCollection")
				.getOrCreate();
		
		JavaSparkContext jsc = new JavaSparkContext(spark.sparkContext());
		
		JavaRDD<Document> documents = jsc.parallelize(asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10))
				.map(new Function<Integer, Document>() {

					private static final long serialVersionUID = 1L;

					public Document call(final Integer i) throws Exception {
						return Document.parse("{test: " + i + "}");
					}
				});
		
		MongoSpark.save(documents);
		
		jsc.close();
	}

}
