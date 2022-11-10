package com.ju4t.bigdata.spark.core.rdd.operator.tarnsform

import org.apache.spark.{SparkConf, SparkContext}

object Spark78_case {
  def main(args: Array[String]): Unit = {
    val sparkConf = new SparkConf().setMaster("local[*]").setAppName("Opeator")
    val sc = new SparkContext(sparkConf)

    // 统计处每一个省份每个广告被点击数量排行榜的Top3
    //
    // 缺什么补什么，多什么删什么

    // TODO 案列实操

    // 1. 获取：原始数据 时间 省份 城市 姓名 广告ID
    val dataRDD = sc.textFile("data/agent.log")

    // 2. 转换：将原始数据进行结构的转换，方便我们的统计
    // 原始数据 时间 省份 城市 姓名 广告ID
    // ((省份, 广告ID), 1)
    val mapRDD = dataRDD.map(
      line => {
        val datas = line.split(" ")
        ((datas(1), datas(4)), 1)
      }
    )

    // 3. 分组聚合：对转换结果进行分组聚合
    // ((省份, 广告ID), 1)  => ((省份, 广告ID), sum)
    val reduceRDD = mapRDD.reduceByKey(_ + _)

    // 4. 结构转换：将分组聚合对结果进行结构转换
    // ((省份, 广告ID), sum) => (省份, (广告ID, sum))
    val newMapRDD = reduceRDD.map {
      case ((prv, ad), sum) => {
        (prv, (ad, sum))
      }
    }

    // 5.将 结构转换 后的数据根据省份进行分组
    // (省份, [(广告1_ID, sum1), (广告2_ID, sum2)])
    val groupRDD = newMapRDD.groupByKey()

    // 6. 组内数量排序（降序，取前3）
    val resultRDD = groupRDD.mapValues(
      iter => {
        iter.toList.sortBy(_._2)(Ordering.Int.reverse).take(3) // 按照 _2 Int 排序
      }
    )

    // 7. 采集数据，打印在控制台
    resultRDD.collect().foreach(println)

    sc.stop()
  }
}
