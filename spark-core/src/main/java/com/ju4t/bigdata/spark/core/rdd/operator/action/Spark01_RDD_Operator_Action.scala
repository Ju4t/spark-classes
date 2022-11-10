package com.ju4t.bigdata.spark.core.rdd.operator.action

import org.apache.spark.{SparkConf, SparkContext}

object Spark01_RDD_Operator_Action {
  def main(args: Array[String]): Unit = {
    val sparkConf = new SparkConf().setMaster("local[*]").setAppName("Operator")
    val sc = new SparkContext(sparkConf)

    val rdd = sc.makeRDD(List(1, 2, 3, 4))

    // TODO - 行动算子
    // 行动算子，触发作业（Job）执行的方法
    // 底层代码调用的是环境对象runJob方法
    // 底层代码中会创建ActionJob，并提交执行
    rdd.collect()

    //
    sc.stop()
  }

}
