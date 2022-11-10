package com.ju4t.bigdata.spark.streaming

import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}

object SparkStreaming06_State_Transorm {

  def main(args: Array[String]): Unit = {

    val sparkConf = new SparkConf().setMaster("local[*]").setAppName("SparkStreaming")
    val ssc = new StreamingContext(sparkConf, Seconds(3))

    val lines = ssc.socketTextStream("localhost", 9999)

    // transform 方法可以将底层RDD获取到后进行操作
    // 1. DStream功能不完善
    // 2. 需要代码 周期性 执行的
    // 方法：transform 和 map 区别在于Code代码对执行位置

    // Code: Driver端
    val newDS = lines.transform(
      // rdd是一个采集周期产生一个
      rdd => rdd.map(
        // Code: Driver端（周期性执行）
        str => {
          // Code: Executor端
          str
        }
      )
    )

    // Code: Driver端
    val newDS1 = lines.map(
      data => {
        // Code: 这段逻辑是在RDD里执行的，Executor端
        data
      }
    )

    ssc.start()
    ssc.awaitTermination()
  }

}
