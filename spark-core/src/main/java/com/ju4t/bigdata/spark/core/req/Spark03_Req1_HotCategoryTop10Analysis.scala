package com.ju4t.bigdata.spark.core.req

import org.apache.spark.{SparkConf, SparkContext}

object Spark03_Req1_HotCategoryTop10Analysis {
  def main(args: Array[String]): Unit = {
    val sparkConf = new SparkConf().setMaster("local[*]").setAppName("HotCategoryTop10Analysis")
    val sc = new SparkContext(sparkConf)

    // TODO 优化
    // Q：依然存在大量的shuffle操作（reduceByKey）
    // reduceByKey 聚合算子，相同的Spark自身会提供优化，本身就有缓存操作

    // 1. 读取原始日志
    val bhvRDD = sc.textFile("data/bhv/")

    // 2. 将我们的数据转换结构
    //    点击的场合: (品类ID, (1, 0，0))
    //    加车的场合: (品类ID, (0, 1，0))
    //    支付的场合: (品类ID, (0, 0，1))
    val flatRDD = bhvRDD.flatMap(
      bhv => {
        val datas = bhv.split("------")
        if (datas(5) == "click") {
          List((datas(2), (1, 0, 0)))
        } else if (datas(5) == "cart") {
          List((datas(2), (0, 1, 0)))
        } else if (datas(5) == "buy") {
          List((datas(2), (0, 0, 1)))
        } else {
          Nil
        }
      }
    )

    // 3. 将相同的品类ID数据进行分组聚合
    //    支付的场合: (品类ID, (点击量, 加车量，支付量))
    val analysisRDD = flatRDD.reduceByKey {
      (t1, t2) => {
        (t1._1 + t2._1, t1._2 + t2._2, t1._3 + t2._3)
      }
    }

    // 4. 将统计结果根据数量进行降序处理，取TOP10
    val resultRDD = analysisRDD.sortBy(_._2, false).take(10)

    // 5. 将结果采集到控制台打印出来，上面已经take了，所以下面不需要在collect
    resultRDD.foreach(println)

    sc.stop()
  }
}
