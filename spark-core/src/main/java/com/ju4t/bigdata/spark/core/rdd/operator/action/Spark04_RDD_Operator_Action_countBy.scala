package com.ju4t.bigdata.spark.core.rdd.operator.action

import org.apache.spark.{SparkConf, SparkContext}

object Spark04_RDD_Operator_Action_countBy {
  def main(args: Array[String]): Unit = {
    val sparkConf = new SparkConf().setMaster("local[*]").setAppName("Operator")
    val sc = new SparkContext(sparkConf)

    val rdd = sc.makeRDD(List(1, 1, 1, 4), 2)
    // TODO - 行动算子

    // 统计出现的次数
    val result = rdd.countByValue()

    println(result)
    // Map(4 -> 1, 2 -> 1, 1 -> 1, 3 -> 1)


    val rdd2 = sc.makeRDD(
      List(
        ("a", 1), ("a", 2)
      )
    )

    val rdd2CountMap = rdd2.countByKey()
    println(rdd2CountMap)

    sc.stop()
  }

}
