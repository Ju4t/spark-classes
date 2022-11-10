package com.ju4t.bigdata.spark.core.req

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object Spark06_Req3_PageFLowAnalysis {
  def main(args: Array[String]): Unit = {
    val sparkConf = new SparkConf().setMaster("local[*]").setAppName("HotCategoryTop10Analysis")
    val sc = new SparkContext(sparkConf)

    // TODO 页面流转分析
    //      https://www.bilibili.com/video/BV1qy4y1n7n3?p=119&spm_id_from=pageDriver

    // 1. 读取原始日志
    val behaviorRDD = sc.textFile("data/PageFlow/")

    // 数据分解 UserBehavior
    val behaviorDataRDD = behaviorRDD.map {
      behavior => {
        val datas = behavior.split(" ")
        UserBehavior(
          datas(0).toLong,
          datas(1),
          datas(2).toLong
        )
      }
    }
    behaviorDataRDD.cache()

    // TODO 对指定的页面连续跳转进行统计
    val ids = List[Long](1, 2, 3)
    val okflowIds = ids.zip(ids.tail)

    // TODO 计算分母
    val pageIdToCountMap = behaviorDataRDD.filter(
      behavior => {
        // 过滤 非指定的数据
        // init 除最后一个，因为分母一定不存在最后一位
        ids.init.contains(behavior.page_id)
      }
    ).map(
      behavior => {
        (behavior.page_id, 1L)
      }
    ).reduceByKey(_ + _).collect().toMap

    // TODO 计算分子
    // 根据user进行分组
    val userRDD = behaviorDataRDD.groupBy(_.user_id)

    // 分组后 根据访问时间进行分组排序（升序）
    val mvRDD = userRDD.mapValues(
      iter => {
        val sortList = iter.toList.sortBy(_.date)

        // https://www.bilibili.com/video/BV1qy4y1n7n3?p=121&t=349.7
        // [Index, Detail, Order, Pay]
        // [Index, Detail], [Detail, Order], [Order, Pay]
        // [Index-Detail, Detail-Order, Order-Pay]
        // Sliding: 滑窗 可以实现

        // zip: 拉 链
        // [Index, Detail, Order, Pay]
        // [Detail, Order, Pay]
        // [Index-Detail, Detail-Order, Order-Pay]
        val flowIds = sortList.map(_.page_id)
        val pageFlowIds = flowIds.zip(flowIds.tail)

        // 将不合法的页面跳转进过滤
        okflowIds.filter(
          t => {
            okflowIds.contains(t)
          }
        ).map(
          t => {
            (t, 1)
          }
        )
      }
    )

    // map 转换  干掉 用户
    // ((1,2),1)
    val flatRDD = mvRDD.map(_._2).flatMap(List => List)
    // ((1,2),1) =>  ((1,2),sum)
    val dataRDD = flatRDD.reduceByKey(_ + _)

    // TODO 计算单挑的转化率
    //      分子 除以 分母
    dataRDD.foreach {
      case ((pageid1, pageid2), sum) => {
        val lon = pageIdToCountMap.getOrElse(pageid1, 0L)
        println(s"页面${pageid1}->${pageid2}单挑转换率为：" + (sum.toDouble / lon))
      }
    }


    sc.stop()
  }

  // 用户访问动作列表
  case class UserBehavior(
                           user_id: Long, // 用户ID
                           date: String, // 用户的点击日期
                           page_id: Long // 页面ID
                         )

}
