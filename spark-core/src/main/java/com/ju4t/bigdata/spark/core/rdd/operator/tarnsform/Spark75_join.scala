package com.ju4t.bigdata.spark.core.rdd.operator.tarnsform

import org.apache.spark.{SparkConf, SparkContext}

object Spark75_join {
  def main(args: Array[String]): Unit = {
    val sparkConf = new SparkConf().setMaster("local[*]").setAppName("Opeator")
    val sc = new SparkContext(sparkConf)

    val rdd1 = sc.makeRDD(List(
      ("a", 1), ("b", 2), ("c", 3)
    ))
    val rdd2 = sc.makeRDD(List(
      ("b", 5), ("c", 6), ("a", 4)
    ))

    // join：连个不同的数据源的数据，相同key的value会连接在一起
    //       如果两个数据源中的key没有匹配上，那么数据不会出现在结果中
    //       如果连个数据源中的key有多个相同的，会依次匹配，可能会出现笛卡尔乘积，数据量会几何形的增长，会有内存的风险，性能降低


    val joinRDD = rdd1.join(rdd2)

    //    (a,(1,4))
    //    (b,(2,5))
    //    (c,(3,6))

    joinRDD.collect().foreach(println)

    sc.stop()
  }
}
