package com.ju4t.bigdata.spark.core.acc

import org.apache.spark.{SparkConf, SparkContext}

import scala.collection.mutable

object Spark05_Bcc {
  def main(args: Array[String]): Unit = {
    val sparkConf = new SparkConf().setMaster("local[*]").setAppName("ACC")
    val sc = new SparkContext(sparkConf)

    val rdd1 = sc.makeRDD(List(
      ("a", 1), ("b", 2), ("c", 3)
    ))
    //    val rdd2 = sc.makeRDD(List(
    //      ("a", 4), ("b", 5), ("c", 6)
    //    ))

    // join 不推荐使用：笛卡尔乘积，并且影响shuffle性能
    //    val joinRDD = rdd1.join(rdd2)
    //    joinRDD.collect().foreach(println)
    // (a,1), (b,2), (c,3)
    // (a,(1,4)), (b,(2,5)), (c,(3,6))

    val map = mutable.Map(("a", 4), ("b", 5), ("c", 6))

    rdd1.map {
      case (w, c) => {
        val l = map.getOrElse(w, 0)
        (w, (c, l))
      }
    }.collect().foreach(println)

    sc.stop()
  }
}
