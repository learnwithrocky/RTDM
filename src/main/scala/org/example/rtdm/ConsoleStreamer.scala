package org.example.rtdm

import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions.{col, from_json}
import org.apache.spark.sql.types.{StringType, StructType}
import org.elasticsearch.hadoop.cfg.ConfigurationOptions

object ConsoleStreamer {

  val spark = SparkSession.builder()
    .appName( "Real Time Data Mining" )
    .master( "local[*]" )
    .config(ConfigurationOptions.ES_NODES, "localhost")
    .config(ConfigurationOptions.ES_PORT, "9200")
    .config("es.net.ssl","false")
    .getOrCreate()

  def setupLogging() = {
    val rootLogger = Logger.getRootLogger()
    rootLogger.setLevel( Level.ERROR )
  }


  def startStream(topic_name:String,index_name:String,checkpoint_location:String) : Unit = {
    setupLogging()
    val schema: StructType = new StructType()
      .add("eventCode", StringType)
      .add("productName", StringType)
      .add("startDate", StringType)
      .add("endDate", StringType)
      .add("eventDts", StringType)
      .add("countryCode",StringType)
      .add("locale",StringType)
      .add("visitGeo",StringType)
      .add("marketingChannel",StringType)
      .add("mobileWeb",StringType)
      .add("visitorType",StringType)
      .add("userCountry",StringType)
      .add("profession",StringType)
      .add("skill",StringType)
      .add("subscription",StringType)
      .add("signupSource",StringType)
      .add("signupCategory",StringType)
      .add("userLocale",StringType)

    val df = spark
      .readStream
      .format("kafka")
      .option("kafka.bootstrap.servers", "localhost:9092")
      .option( "auto.offset.reset"  , "latest")
      .option( "enable.auto.commit" , "true")
      .option( "group.id"            , "id7")
      .option( "max.partition.fetch.bytes", "248532223000")
      .option( "fetch.message.max.bytes", "247483647000")
      .option("subscribe", topic_name)
      .load()

    df
      .selectExpr("CAST(value AS STRING) as value")
      .select(from_json(col("value"), schema).as("data"))
      .selectExpr("data.*")

          .writeStream
          .outputMode("append")
          .format("console")
          .option("truncate", false)
          .start().awaitTermination()
  }
}