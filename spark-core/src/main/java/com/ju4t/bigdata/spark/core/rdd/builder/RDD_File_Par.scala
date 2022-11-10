package com.ju4t.bigdata.spark.core.rdd.builder

import org.apache.spark.{SparkConf, SparkContext}

object RDD_File_Par {
  def main(args: Array[String]): Unit = {
    // TODO 准备环境
    val sparkConf = new SparkConf().setMaster("local[*]").setAppName("RDD")
    val sc = new SparkContext(sparkConf)

    // TODO 创建RDD
    // textFile 将文件作为数据处理的数据源，默认也可以设置分区
    //    minParttions: 最小分区数量
    //    math.min(defaultParallelism, 2) 即默认2个分区
    // val rdd = sc.textFile("data/WordCount/1.txt")

    // 如果不想使用默认分区数量，设置成2分分区，minPartitions 最小分区数，并非总分区数
    // Spark读取文件，底层还是hadoop读取文件的InputFormat
    // 计算方式：
    //     文件的字节数 totalSize / 分区数量 minPartitions = goalSize
    //     7 / 3 = 2...1 (1.1) + 1 = 3 （实际3个分区）
    // https://www.bilibili.com/video/BV1qy4y1n7n3?p=37
    val rdd = sc.textFile("data/WordCount/1.txt", 2)

    rdd.saveAsTextFile("output")

    // TODO 关闭环境
    sc.stop()
  }
}
