package com.ju4t.bigdata.spark.core.rdd.dependencie

import org.apache.spark.{SparkConf, SparkContext}

object Spark01_RDD_Dep {
  def main(args: Array[String]): Unit = {

    // TODO 建立和Spark框架的连接
    val sparkConf = new SparkConf().setMaster("local").setAppName("WordCount")
    val sc = new SparkContext(sparkConf);

    val lines = sc.textFile("data/WordCount/1.txt")

    // toDebugString 打印血缘关系
    println(lines.dependencies)
    println("*****************")

    val words = lines.flatMap(_.split(" "))
    println(words.dependencies)
    println("*****************")

    val wordToOne = words.map(word => (word, 1))
    println(wordToOne.dependencies)
    println("*****************")

    val wordToSum = wordToOne.reduceByKey(_ + _)
    println(wordToSum.dependencies)
    println("*****************")

    wordToSum.collect().foreach(println)

    // TODO 关闭连接
    sc.stop()
  }
}
