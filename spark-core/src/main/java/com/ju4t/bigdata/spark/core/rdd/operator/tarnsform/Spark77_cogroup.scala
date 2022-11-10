package com.ju4t.bigdata.spark.core.rdd.operator.tarnsform

import org.apache.spark.{SparkConf, SparkContext}

object Spark77_cogroup {
  def main(args: Array[String]): Unit = {
    val sparkConf = new SparkConf().setMaster("local[*]").setAppName("Opeator")
    val sc = new SparkContext(sparkConf)

    val rdd1 = sc.makeRDD(List(
      ("a", 1), ("b", 2) //, ("c", 3)
    ))
    val rdd2 = sc.makeRDD(List(
      ("a", 4), ("b", 5), ("c", 6), ("c", 7)
    ))

    // cogroup: connect + group
    val cgRDD = rdd1.cogroup(rdd2)

    cgRDD.collect().foreach(println)
    //    (a,(CompactBuffer(1),CompactBuffer(4)))
    //    (b,(CompactBuffer(2),CompactBuffer(5)))
    //    (c,(CompactBuffer(),CompactBuffer(6, 7)))

    sc.stop()
  }
}
