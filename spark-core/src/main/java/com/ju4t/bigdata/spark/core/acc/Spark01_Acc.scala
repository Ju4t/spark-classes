package com.ju4t.bigdata.spark.core.acc

import org.apache.spark.{SparkConf, SparkContext}

object Spark01_Acc {
  def main(args: Array[String]): Unit = {
    val sparkConf = new SparkConf().setMaster("local[*]").setAppName("ACC")
    val sc = new SparkContext(sparkConf)

    val rdd = sc.makeRDD(List(1, 2, 3, 4))

    // reduce：分区内和分区间的计算
    // val i = rdd.reduce(_ + _)
    // println(i)
    // 10

    // 不靠谱的叠加，因为是分布式的，Drive 和 Executor
    // 未将 Executor 的计算结果传给Drive，所以sum还是0
    // 讲解：https://www.bilibili.com/video/BV1qy4y1n7n3?p=105&spm_id_from=pageDriver
    //    var sum = 0
    //    rdd.foreach(
    //      num => {
    //        sum += num
    //      }
    //    )
    //    println("sum = " + sum)
    // sum = 0

    // TODO 累加器 解决上面的问题，需要将结果返回来
    // 获取累加器
    // Spark默认就提供了简单数据聚合的累加器
    val sumAcc = sc.longAccumulator("sum")

    // 其他累加器
    // sc.doubleAccumulator()
    // sc.collectionAccumulator()

    rdd.foreach(
      num => {
        // 使用累加器
        sumAcc.add(num)
      }
    )

    println(sumAcc.value)

    sc.stop()
  }
}
