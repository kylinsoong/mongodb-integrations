from pyspark.sql import SparkSession

if __name__ == "__main__":

    spark = SparkSession.builder.appName("myApp") \
            .config("spark.mongodb.input.uri", "mongodb://root:mongo@localhost:27000,localhost:27001,localhost:27002/test?replicaSet=repl-1&authSource=admin").config("spark.mongodb.output.uri", "mongodb://root:mongo@localhost:27000,localhost:27001,localhost:27002/test?replicaSet=repl-1&authSource=admin").config("spark.mongodb.input.collection", "myCollection").getOrCreate()



