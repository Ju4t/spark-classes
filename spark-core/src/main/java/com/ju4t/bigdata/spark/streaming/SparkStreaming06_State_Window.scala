package com.ju4t.bigdata.spark.streaming

import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}

object SparkStreaming06_State_Window {

  def main(args: Array[String]): Unit = {

    val sparkConf = new SparkConf().setMaster("local[*]").setAppName("SparkStreaming")
    val ssc = new StreamingContext(sparkConf, Seconds(3))

    val lines = ssc.socketTextStream("localhost", 9999)

    val wordToOne = lines.map((_, 1))

    // 窗口的范围 必须是 采集周期的整数倍
    // 窗口可以滑动的，但是默认情况下，一个采集周期进行滑动
    // val windowDS = wordToOne.window(Seconds(6))

    // 这样的话，可能会出现重复数据的计算，为了避免这种情况，可以改变滑动的时间（步长）
    // window(窗口时长, 滑动步长)，都必须是采集周期的整数倍
    val windowDS = wordToOne.window(Seconds(6), Seconds(6))

    // 窗口范围内的数据再做 reduceByKey
    val wordCount = windowDS.reduceByKey(_ + _)

    wordCount.print()

    ssc.start()
    ssc.awaitTermination()
  }

}
