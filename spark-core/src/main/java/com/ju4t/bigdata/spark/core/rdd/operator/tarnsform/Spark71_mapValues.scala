package com.ju4t.bigdata.spark.core.rdd.operator.tarnsform

import org.apache.spark.{SparkConf, SparkContext}

object Spark71_mapValues {
  def main(args: Array[String]): Unit = {
    val sparkConf = new SparkConf().setMaster("local[*]").setAppName("Opeator")
    val sc = new SparkContext(sparkConf)

    val rdd = sc.makeRDD(List(
      ("a", 1), ("a", 2), ("b", 3),
      ("b", 4), ("b", 5), ("a", 6)
    ), 2)

    // aggregateByKey 最终当返回数据结果应和初始值当类型保持一致
    //    val agg = rdd.aggregateByKey("")(_ + _, _ + _)
    //    agg.collect().foreach(println)

    // 获取相同key当数据当平均值 => (a,3)(b,4)
    // 统计key出现的次数、v的总数，即可获取平均值
    // https://www.bilibili.com/video/BV1qy4y1n7n3?p=71&spm_id_from=pageDriver
    val newRDD = rdd.aggregateByKey((0, 0))(
      (t, v) => {
        (t._1 + v, t._2 + 1)
      },
      (t1, t2) => {
        (t1._1 + t2._1, t1._2 + t2._2)
      }
    )
    //    newRDD.collect().foreach(println)
    //    (b,(12,3))
    //    (a,(9,3))

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
