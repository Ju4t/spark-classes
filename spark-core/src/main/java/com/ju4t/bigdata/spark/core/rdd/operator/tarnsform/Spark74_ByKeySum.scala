package com.ju4t.bigdata.spark.core.rdd.operator.tarnsform

import org.apache.spark.{SparkConf, SparkContext}

object Spark74_ByKeySum {
  def main(args: Array[String]): Unit = {
    val sparkConf = new SparkConf().setMaster("local[*]").setAppName("Opeator")
    val sc = new SparkContext(sparkConf)

    val rdd = sc.makeRDD(List(
      ("a", 1), ("a", 2), ("b", 3),
      ("b", 4), ("b", 5), ("a", 6)
    ), 2)

    // https://www.bilibili.com/video/BV1qy4y1n7n3?p=74&spm_id_from=pageDriver
    rdd.reduceByKey(_ + _) // wordcount
    rdd.aggregateByKey(0)(_ + _, _ + _) // wordcount
    rdd.foldByKey(0)(_ + _) // wordcount
    rdd.combineByKey(v => v, (x: Int, y) => x + y, (x: Int, y: Int) => x + y) // wordcount


    /*
    reduceByKey：
      combineByKeyWithClassTag[V](
        (v: V) => v,
        func,
        func,
        partitioner)

    aggregateByKey:


     */

    sc.stop()
  }
}
