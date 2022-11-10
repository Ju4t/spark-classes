package com.ju4t.bigdata.spark.core.rdd.builder

import org.apache.spark.{SparkConf, SparkContext}

object RDD_Memory_Par {
  def main(args: Array[String]): Unit = {
    // 配置分区
    // TODO 准备环境
    val sparkConf = new SparkConf().setMaster("local[*]").setAppName("RDD")
    // 配置 makeRDD numSlices，即 spark.default.parallelism
    sparkConf.set("spark.default.parallelism", "5")
    val sc = new SparkContext(sparkConf)

    // TODO 创建RDD
    // RDD的并行度 & 分区
    // makeRDD 方法可以传递第二个参数，这个参数表示分区的数量
    //    也可以不传，RDD 会使用 默认值 defaultParallelism
    //    scheduler.conf.getInt("spark.default.parallelism") 即 SparkConf
    //    如果获取不到，则使用totalCores属性，即当前环境的CPU最大可用核数
    val rdd = sc.makeRDD(
      List(1, 2, 3, 4)
    )

    rdd.saveAsTextFile("output")

    // TODO 关闭环境
    sc.stop()
  }
}
