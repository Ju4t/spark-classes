package com.ju4t.bigdata.spark.core.rdd.operator.tarnsform

import org.apache.spark.{SparkConf, SparkContext}

object OperatorTransformApacheLog {
  def main(args: Array[String]): Unit = {
    val sparkConf = new SparkConf().setMaster("local[*]").setAppName("Operator")
    val sc = new SparkContext(sparkConf)

    // TODO 算子 - map
    val rdd = sc.textFile("data/apache.log")

    // 把一个长的字符串转换成短的字符串
    val mapRDD = rdd.map(
      line => {
        val datas = line.split(" ")
        datas(6)
      }
    )

    mapRDD.foreach(println)

    sc.stop()
  } 
}
