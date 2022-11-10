package com.ju4t.bigdata.spark.core.rdd.operator.tarnsform

import org.apache.spark.{SparkConf, SparkContext}

object Spark76_OuterJoin {
  def main(args: Array[String]): Unit = {
    val sparkConf = new SparkConf().setMaster("local[*]").setAppName("Opeator")
    val sc = new SparkContext(sparkConf)

    val rdd1 = sc.makeRDD(List(
      ("a", 1), ("b", 2), ("c", 3)
    ))
    val rdd2 = sc.makeRDD(List(
      ("a", 4), ("b", 5) //, ("c", 6)
    ))

    rdd1.leftOuterJoin(rdd2).collect().foreach(println)
    //    (a,(1,Some(4)))
    //    (b,(2,Some(5)))
    //    (c,(3,None))

    rdd2.rightOuterJoin(rdd1).collect().foreach(println)
    //    (a,(Some(4),1))
    //    (b,(Some(5),2))
    //    (c,(None,3))

    sc.stop()
  }
}
