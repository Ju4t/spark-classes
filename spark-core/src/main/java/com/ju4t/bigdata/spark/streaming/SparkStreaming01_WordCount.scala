package com.ju4t.bigdata.spark.streaming

import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}

object SparkStreaming01_WordCount {

  def main(args: Array[String]): Unit = {
    // TODO 创建环境对象
    // 第一个参数
    val sparkConf = new SparkConf().setMaster("local[*]").setAppName("SparkStreaming")
    // 第二个参数：设置批量处理的采集周期为3秒（即每多长时间采集一次）
    val ssc = new StreamingContext(sparkConf, Seconds(3))

    // TODO 逻辑执行
    // 获取端口数据
    val lines = ssc.socketTextStream("localhost", 9999)

    val wordCount = lines.flatMap(_.split(" ")).map((_, 1)).reduceByKey(_ + _)

    wordCount.print()

    // TODO 不能关闭环境
    //  由于SparkStreaming采集器是长期执行的任务，所以不能直接关闭
    //  如果main方法执行完毕，应用程序它也会自动结束。所以不能让main方法执行完毕
    // ssc.stop()
    //  1. 启动采集器
    ssc.start()
    //  2. 等待采集器的关闭
    ssc.awaitTermination()

  }

}
