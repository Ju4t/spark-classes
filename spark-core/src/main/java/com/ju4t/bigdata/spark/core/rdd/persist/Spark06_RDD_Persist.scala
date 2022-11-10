package com.ju4t.bigdata.spark.core.rdd.persist

import org.apache.spark.{SparkConf, SparkContext}

object Spark06_RDD_Persist {
  def main(args: Array[String]): Unit = {
    val sparkConf = new SparkConf().setMaster("local[*]").setAppName("Persist")
    val sc = new SparkContext(sparkConf)

    // TODO 案列
    // 1. RDD 中不存储数据
    // 2. 如果一个RDD需要重复使用，那么需要从头开始再执行来获取数据
    // 3. RDD 对象可以重用的，但是数据无法重用

    val list = List("Hello Spark", "Hello Scala")
    val rdd = sc.makeRDD(list)
    val flatRDD = rdd.flatMap(_.split(" "))
    val mapRDD = flatRDD.map(word => {
      (word, 1)
    })

    // 分别使用 cache 和 checkpoint 观察血缘关系的变化


    mapRDD.cache()

    //    sc.setCheckpointDir("cp")
    //    mapRDD.checkpoint()

    println(mapRDD.toDebugString)
    mapRDD.reduceByKey(_ + _).collect().foreach(println)
    println("************************")
    println(mapRDD.toDebugString)


    sc.stop()
  }
}
