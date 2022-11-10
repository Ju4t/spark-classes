package com.ju4t.bigdata.spark.core.rdd.operator.tarnsform

import org.apache.spark.{HashPartitioner, SparkConf, SparkContext}

object OperatorTransformParttitionBy {
  def main(args: Array[String]): Unit = {

    val sparkConf = new SparkConf().setMaster("local[*]").setAppName("Operator")
    val sc = new SparkContext(sparkConf)

    var rdd = sc.makeRDD(List(1, 2, 3, 4))

    // partitionBy 必须要 要kv类型，通过map转化成tuple类型
    var mapRDD = rdd.map((_, 1))

    // RDD => PairRDDFunctions
    // implicit
    // 隐式转换（二次编译），在作用域中找到一个特定的方法，让她可以编译通过
    // partitionBy: 根据指定的分区规则对数据进行重分区
    val newRDD = mapRDD.partitionBy(new HashPartitioner(2))

    newRDD.saveAsTextFile("output")

    sc.stop()
  }
}
