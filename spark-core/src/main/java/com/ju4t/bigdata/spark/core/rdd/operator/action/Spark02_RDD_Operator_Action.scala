package com.ju4t.bigdata.spark.core.rdd.operator.action

import org.apache.spark.{SparkConf, SparkContext}

object Spark02_RDD_Operator_Action {
  def main(args: Array[String]): Unit = {
    val sparkConf = new SparkConf().setMaster("local[*]").setAppName("Operator")
    val sc = new SparkContext(sparkConf)

    val rdd = sc.makeRDD(List(1, 2, 3, 4))
    // TODO - 行动算子

    // reduce
    val i = rdd.reduce(_ + _)
    println(i)
    // 10

    // collect 方法会将不同分区的数据按照分区顺序采集到Driver端内存中，形成数组
    val ints = rdd.collect()
    println(ints.mkString(","))

    // count 数据源中数据的个数
    println(rdd.count)

    // first 数据源中的第一个数据
    println(rdd.first)

    // take 获取n个数据
    val intsTake = rdd.take(3)
    println(intsTake.mkString(","))

    // intsTakeOrdered
    val rdd1 = sc.makeRDD(List(4,3,2,1))
    val intsTakeOrdered = rdd1.takeOrdered(3)
    println(intsTakeOrdered.mkString(","))


    sc.stop()
  }

}
