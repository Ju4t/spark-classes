package com.ju4t.bigdata.spark.streaming

import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}

object SparkStreaming06_State_Join {

  def main(args: Array[String]): Unit = {

    val sparkConf = new SparkConf().setMaster("local[*]").setAppName("SparkStreaming")
    val ssc = new StreamingContext(sparkConf, Seconds(5))

    val data8888 = ssc.socketTextStream("localhost", 8888)
    val data9999 = ssc.socketTextStream("localhost", 9999)

    val map8888 = data8888.map((_, 1))
    val map9999 = data9999.map((_, 1))

    // 所谓的DStream的Join操作，其实就是两个RDD的join
    // 相同的key合并
    // https://www.bilibili.com/video/BV1qy4y1n7n3?p=195
    val joinDS = map9999.join(map8888)

    joinDS.print()

    ssc.start()
    ssc.awaitTermination()

  }

}
