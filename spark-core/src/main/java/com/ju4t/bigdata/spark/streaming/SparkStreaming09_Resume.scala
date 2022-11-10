package com.ju4t.bigdata.spark.streaming

import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext, StreamingContextState}

object SparkStreaming09_Resume {

  def main(args: Array[String]): Unit = {

    // 从检查点恢复，如果恢复失败，只执行 getActiveOrCreate的第二个参数内容
    val ssc = StreamingContext.getActiveOrCreate("cp", () => {

      val sparkConf = new SparkConf().setMaster("local[*]").setAppName("SparkStreaming")
      val ssc = new StreamingContext(sparkConf, Seconds(3))
      val lines = ssc.socketTextStream("localhost", 9999)
      val wordToOne = lines.map((_, 1))

      wordToOne.print()

      // 返回
      ssc
    })

    ssc.checkpoint("cp")

    ssc.start()
    ssc.awaitTermination()

  }
}
