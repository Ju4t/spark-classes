package com.ju4t.bigdata.spark.streaming


import org.apache.kafka.clients.producer.{KafkaProducer, ProducerConfig, ProducerRecord}

import java.util.Properties
import scala.collection.mutable.ListBuffer
import scala.util.Random

object SparkStreaming10_MockData {

  def main(args: Array[String]): Unit = {

    // 生成模拟数据
    // 格式：timestamp area city userid adid
    // 含义：时间戳     区域  城市  用户    广告

    // Application => Kafka => SparkStreaming = Analysis

    val prop = new Properties()
    val broker = "localhost:9092"
    val topic = "topic"
    prop.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, broker)
    prop.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer")
    prop.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer")

    val producer = new KafkaProducer[String, String](prop)

    while (true) {
      mockdata().foreach(
        data => {
          // 向 Kafka 中生成数据
          val record = new ProducerRecord[String, String](topic, data)
          // print(data)
          producer.send(record)
        }
      )
      Thread.sleep(2000)
    }

  }

  def mockdata() = {
    val list = ListBuffer[String]()
    val areaList = ListBuffer[String]("华北", "华北", "华南")
    val cityList = ListBuffer[String]("北京", "上海", "深圳")
    for (i <- 1 to 30) {
      val area = areaList(new Random().nextInt(3))
      val city = cityList(new Random().nextInt(3))
      val userId = new Random().nextInt(6) + 1
      val adId = new Random().nextInt(6) + 1
      list.append(s"${System.currentTimeMillis()} ${area} ${city} ${userId} ${adId} ")
    }
    list
  }
}
