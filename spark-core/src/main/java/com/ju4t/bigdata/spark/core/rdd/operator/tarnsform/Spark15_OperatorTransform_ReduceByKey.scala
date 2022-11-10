package com.ju4t.bigdata.spark.core.rdd.operator.tarnsform

import org.apache.spark.{SparkConf, SparkContext}

object Spark15_OperatorTransform_ReduceByKey {
  def main(args: Array[String]): Unit = {
    val sparkConf = new SparkConf().setMaster("local[*]").setAppName("Operator")
    val sc = new SparkContext(sparkConf)

    val rdd = sc.makeRDD(List(
      ("a", 1),
      ("a", 2),
      ("a", 3),
      ("b", 4)
    ))


    // TUDO 算子 （Key - Value 类型）
    // reduceeBykey：相同的key的数据进行value数据的聚合操作
    // scala语言中一般的聚合操作都是两两聚合，spark基于scala开发，所以聚合操作也是两两聚合
    // 【1，2，3】
    // 【3，3】
    // 【6】
    // reduceByKey 中如果key的数据只有一个，是不会参与运算的
    val reduceRDD = rdd.reduceByKey((x: Int, y: Int) => {
      println(s"x=${x}, y=${y}")  // reduceByKey 中如果key的数据只有一个，是不会参与运算的
      x + y
    })

    reduceRDD.collect().foreach(println)

    // 关闭
    sc.stop()

  }

}
