package com.ju4t.bigdata.spark.core.req

import org.apache.spark.rdd.RDD
import org.apache.spark.util.AccumulatorV2
import org.apache.spark.{SparkConf, SparkContext}

import scala.collection.mutable

object Spark05_Req2_HotCategoryTop10UserAnalysis {
  def main(args: Array[String]): Unit = {
    val sparkConf = new SparkConf().setMaster("local[*]").setAppName("HotCategoryTop10Analysis")
    val sc = new SparkContext(sparkConf)

    // TODO Top10分类中每个品类的top10活跃用户统计
    //      需求一上增加增加 每个品类 用户的 点击 统计

    // 1. 读取原始日志
    val bhvRDD = sc.textFile("data/bhv/")
    bhvRDD.cache()
    val top10Ids = top10Category(bhvRDD)

    // 1. 过滤原始数据，保留点击和前10品类ID
    val filterBhvRDD = bhvRDD.filter(
      bhv => {
        val datas = bhv.split("------")
        if (datas(5) == "click") {
          top10Ids.contains(datas(2))
        } else {
          false
        }
      }
    )

    // 2. 根据品类ID和用户ID进行点击量的统计
    val reduceRDD = filterBhvRDD.map(
      bhv => {
        val datas = bhv.split("------")
        // datas(5) category_id
        // datas(3) user_id
        // 1 点击里一次
        ((datas(2), datas(3)), 1)
      }
    ).reduceByKey(_ + _)

    // 3. 将统计的结果进行结构的转换
    // ((品类ID, 用户ID), sum) => (品类ID,(用户ID, sum))
    val mapRDD = reduceRDD.map {
      case ((cid, user_id), sum) => {
        (cid, (user_id, sum))
      }
    }

    // 4. 相同的品类进行分组
    val groupRDD = mapRDD.groupByKey()

    // 5. 将分组后的数据进点击量的排序，取top
    val resultRDD = groupRDD.mapValues(
      iter => {
        iter.toList.sortBy(_._2)(Ordering.Int.reverse).take(10)
      }
    )

    resultRDD.collect().foreach(println)

    sc.stop()
  }

  def top10Category(bhvRDD: RDD[String]) = {
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

    val analysisRDD = flatRDD.reduceByKey {
      (t1, t2) => {
        (t1._1 + t2._1, t1._2 + t2._2, t1._3 + t2._3)
      }
    }

    // 返回，再词过滤，只需要top10的分类ID即可，不需要其他数据.map(_._1)
    analysisRDD.sortBy(_._2, false).take(10).map(_._1)
  }

}
