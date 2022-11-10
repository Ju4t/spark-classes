package com.ju4t.bigdata.spark.core.rdd.operator.tarnsform

import org.apache.spark.{SparkConf, SparkContext}

object OperatorTransform {
  def main(args: Array[String]): Unit = {
    val sparkConf = new SparkConf().setMaster("local[*]").setAppName("Operator")
    val sc = new SparkContext(sparkConf)

    // TODO 算子 - map

    val rdd = sc.makeRDD(List(1, 2, 3, 4))
    // 需求：将1234，转换成2468

    // 转换函数
    def mapFunction(num: Int) = {
      num * 2
    }

    // val mapRDD = rdd.map(mapFunction)
    // 简化代码
    // val mapRDD = rdd.map((num: Int) => {num * 2})
    // 再简化：
    // 当函数当内用只有一行当时候{}可以省略
    // val mapRDD = rdd.map((num: Int) => num*2)

    // 当 参数当类型可以自动推断出来，类型也可以省略
    // val mapRDD = rdd.map((num) => num*2)

    // 当列表参数里当参数只有一个，小括号可以省略
    // val mapRDD = rdd.map(num => num*2)

    // 当 参数 在逻辑中只出现一次，且按照顺序出现的 前面的 num可以省略，把num用_代替
    val mapRDD = rdd.map(_*2)

    mapRDD.collect().foreach(println)

    sc.stop()
  }
}
