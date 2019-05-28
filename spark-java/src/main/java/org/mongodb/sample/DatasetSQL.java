package org.mongodb.sample;

import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

import com.mongodb.spark.MongoSpark;

import static org.mongodb.sample.Constants.TEST_DB_URL;

public class DatasetSQL {

	public static void main(String[] args) {

		SparkSession spark = SparkSession.builder()
				.master("local")
				.appName("MongoSparkConnectorIntro")
				.config("spark.mongodb.input.uri", TEST_DB_URL)
				.config("spark.mongodb.output.uri", TEST_DB_URL)
				.config("spark.mongodb.input.collection", "characters")
				.getOrCreate();
		
		JavaSparkContext jsc = new JavaSparkContext(spark.sparkContext());
		
		/**
		 * Load data and infer schema, disregard toDF() name as it returns Dataset
		 */
		Dataset<Row> implicitDS = MongoSpark.load(jsc).toDF();
		implicitDS.printSchema();
		implicitDS.show();
		
		/**
		 * Load data with explicit schema
		 */
		Dataset<Character> explicitDS = MongoSpark.load(jsc).toDS(Character.class);
	    explicitDS.printSchema();
	    explicitDS.show();
	    
	    /**
	     * Create the temp view and execute the query
	     */
	    explicitDS.createOrReplaceTempView("characters");
	    Dataset<Row> centenarians = spark.sql("SELECT name, age FROM characters WHERE age >= 100");
	    centenarians.show();
	    
	    MongoSpark.write(centenarians).option("collection", "hundredClub").mode("overwrite").save();
	    
		jsc.close();
	}

}
