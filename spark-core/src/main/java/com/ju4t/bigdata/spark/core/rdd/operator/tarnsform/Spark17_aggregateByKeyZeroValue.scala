package com.ju4t.bigdata.spark.core.rdd.operator.tarnsform

import org.apache.spark.{SparkConf, SparkContext}

object Spark17_aggregateByKeyZeroValue {
  def main(args: Array[String]): Unit = {
    val sparkConf = new SparkConf().setMaster("local[*]").setAppName("Opeator")
    val sc = new SparkContext(sparkConf)

    val rdd = sc.makeRDD(List(
      ("a", 1), ("a", 2), ("b", 3),
      ("b", 4), ("b", 5), ("a", 6)
    ), 2)

    //    【1，2】，【3，4】
    //    【2】，【4】
    //    【6】

    // aggregateByKey 存在函数的柯里化
    // 第一个参数列表，他表示为初始值
    //     主要用于当我们遇到第一个key当时候，和value进行分区内计算
    // 第二个参数需要传递2个参数
    //      第1个参数表示分区内计算规则
    //      第2个参数表示分区间计算规则
    rdd.aggregateByKey(5)(
      (x, y) => math.max(x, y), // 分区内计算规则
      (x, y) => x + y // 分区间计算规则
    ).collect().foreach(println)

    //    rdd.aggregateByKey(5)(
    //      (x, y) => x + y, // 分区内计算规则
    //      (x, y) => x + y // 分区间计算规则
    //    ).collect().foreach(println)

    // 简写
    rdd.aggregateByKey(5)(_ + _, _ + _).collect().foreach(println)

    sc.stop()
  }
}
