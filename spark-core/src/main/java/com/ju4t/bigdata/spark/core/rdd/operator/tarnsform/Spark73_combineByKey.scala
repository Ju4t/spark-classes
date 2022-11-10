package com.ju4t.bigdata.spark.core.rdd.operator.tarnsform

import org.apache.spark.{SparkConf, SparkContext}

object Spark73_combineByKey {
  def main(args: Array[String]): Unit = {
    val sparkConf = new SparkConf().setMaster("local[*]").setAppName("Opeator")
    val sc = new SparkContext(sparkConf)

    val rdd = sc.makeRDD(List(
      ("a", 1), ("a", 2), ("b", 3),
      ("b", 4), ("b", 5), ("a", 6)
    ), 2)

    // combineByKey：需要三个参数
    //              第1个参数：将相同key的第一个数据进行结构的转换，实现我们的操作
    //              第2个参数：分区内的计算规则
    //              第3个参数：分区间的计算规则
    val newRDD = rdd.combineByKey(
      v => (v, 1),
      (t: (Int, Int), v) => {
        (t._1 + v, t._2 + 1)
      },
      (t1: (Int, Int), t2: (Int, Int)) => {
        (t1._1 + t2._1, t1._2 + t2._2)
      }
    )

    // key 不变，对v进行操作
    val resultRDD = newRDD.mapValues {
      case (num, cnt) => {
        num / cnt
      }
    }

    resultRDD.collect().foreach(println)
    //    (b,4)
    //    (a,3)

    sc.stop()
  }
}
