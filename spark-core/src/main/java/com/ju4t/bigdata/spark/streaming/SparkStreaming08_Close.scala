package com.ju4t.bigdata.spark.streaming

import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext, StreamingContextState}

object SparkStreaming08_Close {

  def main(args: Array[String]): Unit = {

    /*
      线程的关闭：
      val thread = new Thread()
      thread.start()
      thread.stop() // 强制关闭
     */

    val sparkConf = new SparkConf().setMaster("local[*]").setAppName("SparkStreaming")
    val ssc = new StreamingContext(sparkConf, Seconds(3))

    ssc.checkpoint("cp")

    val lines = ssc.socketTextStream("localhost", 9999)

    val wordToOne = lines.map((_, 1))

    wordToOne.print()


    ssc.start()
    ssc.awaitTermination()

    // 如果想要关闭采集器，那么需要创建新的线程
    // 而且需要在第三方程序中增加关闭的状态
    // 多线程：https://www.bilibili.com/video/BV1qy4y1n7n3?p=199&spm_id_from=pageDriver
    new Thread(
      new Runnable {
        override def run(): Unit = {
          // 优雅地关闭
          // 计算节点不在接受新的数据，而是将现有的数据处理完毕，然后关闭
          // MySQL: Table => Row => data
          // Redis: Data(k-v)
          while (true) {
            if (true) { // 某个条件
              // 获取 SparkStreaming 状态
              val state = ssc.getState()
              if (state == StreamingContextState.ACTIVE) { // 当前环境的状态是开启
                ssc.stop(true, true)
                // 停止线程
                System.exit(0)
              }
            }
            // 休眠5s
            Thread.sleep(5000)
          }
        }
      }
    ).start()

  }

}
