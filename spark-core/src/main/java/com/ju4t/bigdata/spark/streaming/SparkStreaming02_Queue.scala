package com.ju4t.bigdata.spark.streaming

import org.apache.spark.SparkConf
import org.apache.spark.rdd.RDD
import org.apache.spark.streaming.{Seconds, StreamingContext}

import scala.collection.mutable

object SparkStreaming02_Queue {

  def main(args: Array[String]): Unit = {
    // TODO 创建环境对象
    // 第一个参数
    val sparkConf = new SparkConf().setMaster("local[*]").setAppName("SparkStreaming")
    // 第二个参数：设置批量处理的采集周期为3秒（即每多长时间采集一次）
    val ssc = new StreamingContext(sparkConf, Seconds(3))

    // TODO 逻辑执行

    // 创建RDD队列
    val rddQueue = new mutable.Queue[RDD[Int]]()

    // 创建QueueInputDStream
    val inputStream = ssc.queueStream(rddQueue, oneAtATime = false)

    // 处理队列
    val reducedStream = inputStream.map((_, 1)).reduceByKey(_ + _)

    reducedStream.print()

    // 开始
    ssc.start()

    //
    for (i <- 1 to 5) {
      rddQueue += ssc.sparkContext.makeRDD(1 to 300, 10)
      Thread.sleep(200)
    }

    ssc.awaitTermination()

  }

}
