package com.ju4t.bigdata.spark.core.rdd.builder

import org.apache.spark.{SparkConf, SparkContext}

object RDD_File1 {
  def main(args: Array[String]): Unit = {
    // https://www.bilibili.com/video/BV1qy4y1n7n3?p=34
    // TODO 准备环境
    val sparkConf = new SparkConf().setMaster("local[*]").setAppName("RDD")
    val sc = new SparkContext(sparkConf)

    // TODO 创建RDD
    // 从文件中创建RDD，将文件中的数据作为处理的数据流
    // textFile：以行为单位来读取数据
    // wholeTextFiles：以文件为单位来读取数据
    // 读取的结果表示为元组，第一个表示文件路径，第二个表示文件内容
    val rdd = sc.wholeTextFiles("data/WordCount")

    rdd.collect().foreach(println)

    // TODO 关闭环境
    sc.stop()
  }
}
