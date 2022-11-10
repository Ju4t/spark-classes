package com.ju4t.bigdata.spark.core.wc

import org.apache.spark.{SparkConf, SparkContext}

object Spark02_WordCount {
  def main(args: Array[String]): Unit = {
    // Application
    // Spark框架
    // TODO 建立和Spark框架的连接
    val sparkConf = new SparkConf().setMaster("local").setAppName("WordCount")
    val sc = new SparkContext(sparkConf);

//    // TODO 执行业务操作
//    val lines = sc.textFile("data/WordCount")
//    val words = lines.flatMap(_.split(" "))
//
//    // 优化
//    val wordToOne = words.map(
//      word => (word, 1)
//    )
//
//    // spark 提供给了更多对功能，将分组和聚合使用一个方法来实现
//    // reduceByKey:相同的key的数据，可以对value进行reduce聚合
//    // wordToOne.reduceByKey((x,y)=>{ x + y })
//    val wordToCount = wordToOne.reduceByKey(_ + _) // 上面对简写
//    sc.textFile("data/WordCount").flatMap(_.split(" ")).map((_,1)).reduceByKey(_+_).collect()
//    val array = wordToCount.collect()

    // 这一句等于上面
    val array = sc.textFile("data/WordCount").flatMap(_.split(" ")).map((_,1)).reduceByKey(_+_).collect();
    array.foreach(println)

    // TODO 关闭连接
    sc.stop()
  }
}
