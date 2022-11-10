package com.ju4t.bigdata.spark.core.rdd.operator.action

import org.apache.spark.{SparkConf, SparkContext}

object Spark05_RDD_Operator_Action_save {
  def main(args: Array[String]): Unit = {
    val sparkConf = new SparkConf().setMaster("local[*]").setAppName("Operator")
    val sc = new SparkContext(sparkConf)

    val rdd = sc.makeRDD(List(
      ("a", 1), ("a", 2), ("a", 3)
    ), 2)

    // TODO - 行动算子
    rdd.saveAsTextFile("output")
    rdd.saveAsObjectFile("output1")

    // saveAsSequenceFile 要求数据的格式必须为K-V类型
    rdd.saveAsSequenceFile("output2")

    sc.stop()
  }

}
