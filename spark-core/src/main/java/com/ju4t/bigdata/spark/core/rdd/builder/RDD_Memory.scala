package com.ju4t.bigdata.spark.core.rdd.builder

import org.apache.spark.{SparkConf, SparkContext}

object RDD_Memory {
  def main(args: Array[String]): Unit = {
    // https://www.bilibili.com/video/BV1qy4y1n7n3?p=32
    // TODO 准备环境
    val sparkConf = new SparkConf().setMaster("local[*]").setAppName("RDD")
    val sc = new SparkContext(sparkConf)

    // TODO 创建RDD
    // 从内存中创建RDD，将内存中集合的数据作为处理的数据流
    // https://www.bilibili.com/video/BV1qy4y1n7n3?p=32&t=306.5
    val seq = Seq[Int](1,2,3,4)

    // parallelize：并行
    // val rdd = sc.parallelize(seq)
    // makeRDD方法在底层实现时其实就是调用了RDD parallelize方法
    val rdd = sc.makeRDD(seq)

    rdd.collect().foreach(println)

    // TODO 关闭环境
    sc.stop()
  }
}
