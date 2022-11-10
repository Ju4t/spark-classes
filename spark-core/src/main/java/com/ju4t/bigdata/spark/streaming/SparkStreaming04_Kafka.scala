package com.ju4t.bigdata.spark.streaming

import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.spark.SparkConf
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.kafka010.{ConsumerStrategies, KafkaUtils, LocationStrategies}
import org.apache.spark.streaming.receiver.Receiver
import org.apache.spark.streaming.{Seconds, StreamingContext}

import scala.util.Random

object SparkStreaming04_Kafka {

  def main(args: Array[String]): Unit = {

    val sparkConf = new SparkConf().setMaster("local[*]").setAppName("SparkStreaming")
    val ssc = new StreamingContext(sparkConf, Seconds(3))

    val broker = "localhost:9092"
    val groupId = "test-consumer-group"
    val topic = "topic"

    val kafkaParams: Map[String, Object] = Map[String, Object](
      ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG -> broker,
      ConsumerConfig.GROUP_ID_CONFIG -> groupId, // GROUP_ID_CONFIG
      "key.deserializer" -> "org.apache.kafka.common.serialization.StringDeserializer",
      "value.deserializer" -> "org.apache.kafka.common.serialization.StringDeserializer"
    )

    val kafkaDataDS = KafkaUtils.createDirectStream[String, String](
      ssc,
      LocationStrategies.PreferConsistent, // 我们的采集节点和计算的节点该如何匹配，类似于首选位置
      ConsumerStrategies.Subscribe[String, String](Set(topic), kafkaParams)
    )

    kafkaDataDS.map(_.value()).print()

    ssc.start()
    ssc.awaitTermination()
  }

}
