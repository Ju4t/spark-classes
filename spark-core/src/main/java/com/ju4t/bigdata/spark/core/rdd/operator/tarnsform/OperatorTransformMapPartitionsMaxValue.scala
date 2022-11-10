package com.ju4t.bigdata.spark.core.rdd.operator.tarnsform

import org.apache.spark.{SparkConf, SparkContext}

object OperatorTransformMapPartitionsMaxValue {
  def main(args: Array[String]): Unit = {
    val sparkConf = new SparkConf().setMaster("local[*]").setAppName("Operator")
    val sc = new SparkContext(sparkConf)

    // TODO 算子 - map
    val rdd = sc.makeRDD(List(1, 2, 3, 4), 2)

    // 需求：求每个分区李的最大数
    val mapRDD = rdd.mapPartitions(
      iter => {
        List(iter.max).iterator
      }
    )

    mapRDD.collect().foreach(println)

    sc.stop()
  } 
}
