package com.ju4t.bigdata.spark.test

import org.apache.spark.{SparkConf, SparkContext}

object Spark_WordCount {
  def main(args: Array[String]): Unit = {
    val sparkConf = new SparkConf().setMaster("local[*]").setAppName("WordCount")
    val sc = new SparkContext(sparkConf)

    val rdd = sc.textFile("data/WordCount")

    val wordCount = rdd.flatMap(_.split(" ")).map((_, 1)).reduceByKey(_ + _).sortBy(e => (e._2, e._1), false).take(3)

    wordCount.foreach(println)

    // TODO Stop
    sc.stop()
  }
}
