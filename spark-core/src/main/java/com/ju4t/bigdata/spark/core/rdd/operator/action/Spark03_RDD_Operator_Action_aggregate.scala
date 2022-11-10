package com.ju4t.bigdata.spark.core.rdd.operator.action

import org.apache.spark.{SparkConf, SparkContext}

object Spark03_RDD_Operator_Action_aggregate {
  def main(args: Array[String]): Unit = {
    val sparkConf = new SparkConf().setMaster("local[*]").setAppName("Operator")
    val sc = new SparkContext(sparkConf)

    val rdd = sc.makeRDD(List(1, 2, 3, 4), 2)
    // TODO - 行动算子

    // aggregate  分区内、分区间算法
    val result1 = rdd.aggregate(0)(_ + _, _ + _)
    println(result1)
    // 10

    val result2 = rdd.aggregate(10)(_ + _, _ + _)
    // 10 + 1 + 2 = 13    分区1
    // 10 + 3 + 4 = 17    分区2
    // 13 + 17 + 10 = 40  分区间
    // aggregateByKey 初始值只参与分区内计算 30
    // aggregate 初始值参与分区内和分区间计算 40
    println(result2)

    sc.stop()
  }

}
