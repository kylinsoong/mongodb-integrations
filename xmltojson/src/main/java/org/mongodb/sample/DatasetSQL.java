package org.mongodb.sample;

import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

import com.mongodb.spark.MongoSpark;

public class DatasetSQL {
	
	public static final String TEST_DB_URL = "mongodb://root:mongo@localhost:27000,localhost:27001,localhost:27002/bos?replicaSet=repl-1&authSource=admin";


	public static void main(String[] args) {

		SparkSession spark = SparkSession.builder()
				.master("local")
				.appName("MongoSparkConnectorIntro")
				.config("spark.mongodb.input.uri", TEST_DB_URL)
				.config("spark.mongodb.output.uri", TEST_DB_URL)
				.config("spark.mongodb.input.collection", "xmltojson")
				.getOrCreate();
		
		JavaSparkContext jsc = new JavaSparkContext(spark.sparkContext());
		
		/**
		 * Load data and infer schema, disregard toDF() name as it returns Dataset
		 */
		Dataset<Row> implicitDS = MongoSpark.load(jsc).toDF();
		implicitDS.printSchema();
		implicitDS.show();
		
		
	    
	    /**
	     * Create the temp view and execute the query
	     */
		implicitDS.createOrReplaceTempView("xmltojson");
	    Dataset<Row> centenarians = spark.sql("SELECT c1, c2, c3, c4, c5, c6 FROM xmltojson WHERE c1 == 20171010");
	    centenarians.show();
	   
	    
		jsc.close();
	}

}
