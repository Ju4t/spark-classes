package com.ju4t.bigdata.spark.streaming

import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.spark.SparkConf
import org.apache.spark.streaming.kafka010.{ConsumerStrategies, KafkaUtils, LocationStrategies}
import org.apache.spark.streaming.{Seconds, StreamingContext}

object SparkStreaming05_State {

  def main(args: Array[String]): Unit = {

    val sparkConf = new SparkConf().setMaster("local[*]").setAppName("SparkStreaming")
    val ssc = new StreamingContext(sparkConf, Seconds(3))

    ssc.checkpoint("cp")

    val datas = ssc.socketTextStream("localhost", 9999)
    // 无状态：只对当前采集后期内对数据进行处理
    // val wordCount = datas.map((_, 1)).reduceByKey(_ + _)
    // wordCount.print()

    // 有状态：在某些场合下需要保留数据统计结果（状态），实现数据对汇总
    // 使用有状态操作时，需要设置 checkpoint
    // updateStateByKey：根据key对数据对状态进行更新
    // 传递对参数中含有两个值：
    //    第一个值表示相同key对value数据
    //    第二个值表示缓冲区相同key对value数据
    val state = datas.map((_, 1)).updateStateByKey(
      (seq: Seq[Int], buff: Option[Int]) => {
        val newCount = buff.getOrElse(0) + seq.sum
        Option(newCount)
      }
    )
    state.print()

    ssc.start()
    ssc.awaitTermination()
  }

}
