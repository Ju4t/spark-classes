package com.ju4t.bigdata.spark.streaming

import org.apache.spark.SparkConf
import org.apache.spark.rdd.RDD
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.receiver.Receiver
import org.apache.spark.streaming.{Seconds, StreamingContext}

import scala.collection.mutable
import scala.util.Random

object SparkStreaming03_Receiver {

  def main(args: Array[String]): Unit = {

    val sparkConf = new SparkConf().setMaster("local[*]").setAppName("SparkStreaming")
    val ssc = new StreamingContext(sparkConf, Seconds(3))

    // TODO 逻辑执行
    val msgDS = ssc.receiverStream(new MyReceiver())
    msgDS.print()

    ssc.start()
    ssc.awaitTermination()
  }

  /*
    自定义数据采集器
    1. 继承 Receiver，定义泛型
    2. 重写方法
    3. 调用 ssc.receiverStream(new MyReceiver())
   */
  class MyReceiver extends Receiver[String](StorageLevel.MEMORY_ONLY) {

    private var flag = true

    override def onStart(): Unit = {
      new Thread(new Runnable {
        override def run(): Unit = {
          while (flag) {
            // 生产一个随机字符串，演示以下
            val message = "采集的数据为：" + new Random().nextInt(10).toString

            // 存入消息，底层自动封装，级别就是 StorageLevel.MEMORY_ONLY
            store(message)
            Thread.sleep(500)
          }
        }
      }).start()
    }

    override def onStop(): Unit = {
      flag = false;
    }
  }

}
