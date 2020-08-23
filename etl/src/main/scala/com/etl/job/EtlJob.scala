package com.etl.job

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions.col
import org.apache.spark.SparkEnv
import org.apache.spark.metrics.source.StreamingSource
import org.apache.spark.sql.streaming.StreamingQueryListener

object EtlJob {

  def main(args: Array[String]): Unit = {


    val spark: SparkSession = SparkSession
      .builder()
      .master("local[*]")
      .appName("ETLJob")
      .getOrCreate()


    val querySource = new StreamingSource(spark.sparkContext.appName)
    SparkEnv.get.metricsSystem.registerSource(querySource)

    val queryListener: StreamingQueryListener = new StreamingQueryListener {
      override def onQueryStarted(event: StreamingQueryListener.QueryStartedEvent): Unit = {}

      override def onQueryProgress(event: StreamingQueryListener.QueryProgressEvent): Unit = {
        querySource.updateProgress(event.progress)
      }

      override def onQueryTerminated(event: StreamingQueryListener.QueryTerminatedEvent): Unit = {}
    }
    spark.streams.addListener(queryListener)

    spark.sparkContext.setLogLevel("WARN")

    val stream = spark
      .readStream
      .format("kafka")
      .option("startingOffsets", "earliest")
      .option("kafka.bootstrap.servers", "broker:29092")
      .option("subscribe", "mysql-20")
      .load()

    val schemaRegistryConfig = Map(
      "schema.registry.url" -> "http://schema-registry:8081",
      "value.schema.id" -> "latest",
      "schema.registry.topic" -> "mysql-20",
      "value.schema.naming.strategy" -> "topic.name")

    import za.co.absa.abris.avro.functions.from_confluent_avro

    val deserialized = stream.select(

      from_confluent_avro(col("value"), schemaRegistryConfig) as 'data)

    deserialized.printSchema()

    deserialized
      .select("data.*")
      .writeStream
      .partitionBy("volume_type_id")
      .format("parquet")
      .option("path", "/tmp/etl-data")
      .option("checkpointLocation", "/tmp/checkpoint")
      .start()
      .awaitTermination()
  }

}
