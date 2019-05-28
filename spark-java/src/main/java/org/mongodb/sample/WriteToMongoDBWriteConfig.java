package org.mongodb.sample;

import java.util.HashMap;
import java.util.Map;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.sql.SparkSession;
import org.bson.Document;

import static java.util.Arrays.asList;

import com.mongodb.spark.MongoSpark;
import com.mongodb.spark.config.WriteConfig;
import static org.mongodb.sample.Constants.TEST_DB_URL;

public class WriteToMongoDBWriteConfig {

	public static void main(String[] args) {

		SparkSession spark = SparkSession.builder()
				.master("local")
				.appName("MongoSparkConnectorIntro")
				.config("spark.mongodb.output.uri", TEST_DB_URL)
				.config("spark.mongodb.output.collection", "myCollection")
				.getOrCreate();
		
		JavaSparkContext jsc = new JavaSparkContext(spark.sparkContext());
		
		Map<String, String> writeOverrides = new HashMap<String, String>();
	    writeOverrides.put("collection", "spark");
	    writeOverrides.put("writeConcern.w", "majority");
	    WriteConfig writeConfig = WriteConfig.create(jsc).withOptions(writeOverrides);
	    
		JavaRDD<Document> sparkDocuments = jsc.parallelize(asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10))
				.map(new Function<Integer, Document>() {

					private static final long serialVersionUID = 1065359389887607303L;

					public Document call(final Integer i) throws Exception {
						return Document.parse("{spark: " + i + "}");
					}
				});
		
		MongoSpark.save(sparkDocuments, writeConfig);
		
		jsc.close();
	}

}
