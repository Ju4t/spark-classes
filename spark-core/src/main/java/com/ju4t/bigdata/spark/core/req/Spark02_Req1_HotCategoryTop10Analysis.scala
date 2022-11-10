package com.ju4t.bigdata.spark.core.req

import org.apache.spark.{SparkConf, SparkContext}

object Spark02_Req1_HotCategoryTop10Analysis {
  def main(args: Array[String]): Unit = {

    // TODO 优化
    // Q1: bhvRDD 重复使用
    //     使用cache
    // Q2: cogroup 有可能存在shuffle，按如下方式两两重新组合
    //     (品类ID，点击数量) => (品类ID，点击数量，0，0)
    //     (品类ID，加车数量) => (品类ID，0，加车数量，0)
    //                     => (品类ID，(点击数量，加车数量，0))
    //     (品类ID，支付数量) => (品类ID，0，0，支付数量)
    //                     => (品类ID，(点击数量，加车数量，支付数量))
    //     (品累ID，点击数量，加车数量，支付数量)

    val sparkConf = new SparkConf().setMaster("local[*]").setAppName("HotCategoryTop10Analysis")
    val sc = new SparkContext(sparkConf)

    // 1. 读取原始日志
    val bhvRDD = sc.textFile("data/bhv/")
    bhvRDD.cache()

    // 2. 统计品类的点击量：（品类ID，点击数量）
    // 2021-12-16_16/12/2021 16:40:19.911735_10178_18729_16/12/2021 16:40:19.911735_click_1
    // 先过滤数据
    val clickRDD = bhvRDD.filter(
      bhv => {
        val datas = bhv.split("------")
        datas(5) == "click"
      }
    )

    // 再统计
    val clickCountRDD = clickRDD.map(
      bhv => {
        val datas = bhv.split("------")
        (datas(2), 1)
      }
    ).reduceByKey(_ + _)

    // 3. 统计商品的加车数量：（品类ID，加车数量）
    val cartRDD = bhvRDD.filter(
      bhv => {
        val datas = bhv.split("------")
        datas(5) == "cart"
      }
    )
    val cartCountRDD = cartRDD.map(
      bhv => {
        val datas = bhv.split("------")
        (datas(2), 1)
      }
    ).reduceByKey(_ + _)

    // 4. 统计品类的：（品类ID，支付数量）
    val buyRDD = bhvRDD.filter(
      bhv => {
        val datas = bhv.split("------")
        datas(5) == "buy"
      }
    )
    val buyCountRDD = buyRDD.map(
      bhv => {
        val datas = bhv.split("------")
        (datas(2), 1)
      }
    ).reduceByKey(_ + _)

    // 5. 将品类进行排序，并且取top10
    val rdd1 = clickCountRDD.map {
      case (cid, cnt) => {
        (cid, (cnt, 0, 0))
      }
    }

    val rdd2 = cartCountRDD.map {
      case (cid, cnt) => {
        (cid, (0, cnt, 0))
      }
    }

    val rdd3 = buyCountRDD.map {
      case (cid, cnt) => {
        (cid, (0, 0, cnt))
      }
    }

    // 将3个数据源合并在一起，统一进行聚合计算
    val sourceRDD = rdd1.union(rdd2).union(rdd3)

    val analysisRDD = sourceRDD.reduceByKey(
      (t1, t2) => {
        (t1._1 + t2._1, t1._2 + t2._2, t1._3 + t2._3)
      }
    )

    val resultRDD = analysisRDD.sortBy(_._2, false).take(10)

    // 6. 将结果采集到控制台打印出来，上面已经take了，所以下面不需要在collect
    resultRDD.foreach(println)

    sc.stop()
  }
}
