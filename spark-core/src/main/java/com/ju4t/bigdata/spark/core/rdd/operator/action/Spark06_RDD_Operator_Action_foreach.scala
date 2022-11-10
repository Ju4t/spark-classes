package com.ju4t.bigdata.spark.core.rdd.operator.action

import org.apache.spark.{SparkConf, SparkContext}

object Spark06_RDD_Operator_Action_foreach {
  def main(args: Array[String]): Unit = {
    val sparkConf = new SparkConf().setMaster("local[*]").setAppName("Operator")
    val sc = new SparkContext(sparkConf)

    val rdd = sc.makeRDD(List(1, 2, 3, 4))

    // 下面foreach 其实是Driver端内存集合端循环遍历方法
    rdd.collect().foreach(println)
    println("*******************")
    // 下面foreach 其实是Executor端内存数据的打印
    rdd.foreach(println)

    //    算子：Operator（操作）
    //    - RDD方法和Scala集合对象的方法不一样
    //    - 集合对象的方法都是在同一个节点内存中完成的
    //    - RDD的方法可以将计算逻辑发送到Executor端（分布式节点）执行
    //    - 为了区分不同的处理效果，所以将RDD的方法称之为算子
    //    - RDD方法外部的操作都是在Driver端执行的，而方法内部的逻辑代码是Executor端执行的

    //    1
    //    2
    //    3
    //    4
    //    *******************
    //    2
    //    3
    //    4
    //    1

    sc.stop()
  }

}
