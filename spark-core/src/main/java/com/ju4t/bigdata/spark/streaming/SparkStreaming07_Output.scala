package com.ju4t.bigdata.spark.streaming

import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}

object SparkStreaming07_Output {

  def main(args: Array[String]): Unit = {

    val sparkConf = new SparkConf().setMaster("local[*]").setAppName("SparkStreaming")
    val ssc = new StreamingContext(sparkConf, Seconds(3))

    ssc.checkpoint("cp")

    val lines = ssc.socketTextStream("localhost", 9999)

    val wordToOne = lines.map((_, 1))

    // reduceByKeyAndWindows：当我们的窗口范围比较大，但是滑动幅度比较小，那么可以采用增加数据和删除数据的方式
    // 无需重复计算，提升性能
    val windowDS = wordToOne.reduceByKeyAndWindow(
      (x: Int, y: Int) => { x + y}, // 把新增的数据加进来
      (x: Int, y: Int) => { x - y }, // 把过去的数据减掉
      Seconds(9), Seconds(3)) // 有6秒内容是重复的

    // windowDS.print()

    ssc.start()
    ssc.awaitTermination()
  }

}
