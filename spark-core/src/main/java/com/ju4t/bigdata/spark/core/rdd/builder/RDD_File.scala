package com.ju4t.bigdata.spark.core.rdd.builder

import org.apache.spark.{SparkConf, SparkContext}

object RDD_File {
  def main(args: Array[String]): Unit = {
    // https://www.bilibili.com/video/BV1qy4y1n7n3?p=33
    // TODO 准备环境
    val sparkConf = new SparkConf().setMaster("local[*]").setAppName("RDD")
    val sc = new SparkContext(sparkConf)

    // TODO 创建RDD
    // 从文件中创建RDD，将文件中的数据作为处理的数据流
    // path 路径默认以当前环境的根路径为基准。可以写绝对路径、也可以写相对路径
    // val rdd = sc.textFile("data/WordCount/1.txt")
    // path 路径可以是文件的具体路径，也可以是目录
    val rdd = sc.textFile("data/WordCount")
    // path 路径可以是通配符
    // val rdd = sc.textFile("data/WordCount/1*.txt")
    // path 路径可以是分布式存储系统路径：HDFS、HBase
    // val rdd = sc.textFile("hdfs://hadoop81:8020/test.txt")

    rdd.collect().foreach(println)

    // TODO 关闭环境
    sc.stop()
  }
}
