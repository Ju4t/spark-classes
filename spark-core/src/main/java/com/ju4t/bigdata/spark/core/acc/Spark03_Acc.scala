package com.ju4t.bigdata.spark.core.acc

import org.apache.spark.{SparkConf, SparkContext}

object Spark03_Acc {
  def main(args: Array[String]): Unit = {
    val sparkConf = new SparkConf().setMaster("local[*]").setAppName("ACC")
    val sc = new SparkContext(sparkConf)

    val rdd = sc.makeRDD(List(1, 2, 3, 4))
    // TODO 累加器 解决上面的问题，需要将结果返回来

    val sumAcc = sc.longAccumulator("sum")

    val mapRDD = rdd.map(
      num => {
        // 使用累加器
        sumAcc.add(num)
        num
      }
    )

    // 获取累加器的值
    // 少加：转换算子调用累加器，如果，没有行动算子的话是不会执行
    // 多加：行动算子调用累加器，调用多次，结果就执行多次
    // 一般情况下，累加器会放置在行动算子中
    mapRDD.collect()
    //    mapRDD.collect()
    //    mapRDD.collect()

    println(sumAcc.value)

    sc.stop()
  }
}
