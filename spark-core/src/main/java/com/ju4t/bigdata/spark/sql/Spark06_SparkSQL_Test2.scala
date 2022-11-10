package com.ju4t.bigdata.spark.sql

import org.apache.spark.SparkConf
import org.apache.spark.sql.{Encoder, Encoders, SparkSession, functions}
import org.apache.spark.sql.expressions.Aggregator

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

object Spark06_SparkSQL_Test2 {
  def main(args: Array[String]): Unit = {

    val sparkConf = new SparkConf().setMaster("local[*]").setAppName("sparkSQL")
    val spark = SparkSession
      .builder()
      .enableHiveSupport()
      .config(sparkConf)
      .getOrCreate()


    // TODO 准备 Hive
    spark.sql("create databases spark")
    spark.sql("use spark")

    // TODO 需求
    // https://www.bilibili.com/video/BV1qy4y1n7n3?p=182&spm_id_from=pageDriver
    // 这里的热门商品是从 点击量 的纬度来看的，j计算 各个区域 前三大热门商品，并准备每个在主要城市中的分布比例，超过两个城市用其他显示

    // 查询基本数据
    spark.sql(
      """
        | SELECT
        |	  a.*,
        |   p.product_name,
        |   c.city_name,
        |   c.area
        | FROM
        |   user_visit_action AS a
        |   JOIN product_info AS p ON a.click_product_id = p.product_id
        |   JOIN city_info AS c ON a.city_d = c.city_id
        | WHERE
        |   a.click_product_id > - 1
        |""".stripMargin).createOrReplaceTempView("t1")

    // 根据区域，商品进行聚合
    spark.udf.register("cityRemark", functions.udaf(new CityRemarkUDAF()))
    spark.sql(
      """
        | SELECT
        |   area,
        |   product_name,
        |   count( 1 ) AS clickCnt,
        |   cityRemark(city_name) as cityRemark
        | FROM t1
        |""".stripMargin).createOrReplaceTempView("t2")

    // 区域内对我们对点击数量进行排行
    spark.sql(
      """
        | SELECT
        |   *,
        |   rank() over(partition by area order by clickCnt DESC)  as rank
        | FROM t2
        |""".stripMargin).createOrReplaceTempView("t3")

    // 取前三名
    // 展示数据：禁止截取 truncate = false
    spark.sql(
      """
        | SELECT
        |   *
        | FROM t3 WHERE rank <= 3
        |""".stripMargin).show(false)

    // TODO 关闭
    spark.close()
  }

  case class Buffer(var total: Long, var cityMap: mutable.Map[String, Long])

  // 自定义聚合函数，实现城市对备注功能
  // 1. Aggregator，定义泛型
  //    IN: 城市
  //    BUF: [总点击数量, Map[(city, cnt), (city, cnt), (city, cnt)]]
  //    OUT: 备注信息
  // 2. 重写方法
  class CityRemarkUDAF extends Aggregator[String, Buffer, String] {

    // 缓冲区初始化
    override def zero: Buffer = {
      Buffer(0, mutable.Map[String, Long]())
    }

    // 更新缓冲区
    override def reduce(buff: Buffer, city: String): Buffer = {
      buff.total += 1
      val newCnt = buff.cityMap.getOrElse(city, 0L) + 1
      buff.cityMap.update(city, newCnt)
      buff
    }

    // 合并缓冲区
    override def merge(b1: Buffer, b2: Buffer): Buffer = {
      b1.total += b2.total
      val map1 = b1.cityMap
      val map2 = b2.cityMap

      // 方式一：两个 map 的合并
      b1.cityMap = map1.foldLeft(map2) {
        case (map, (city, cnt)) => {
          val newCount = map.getOrElse(city, 0L) + cnt
          map.update(city, newCount)
          map
        }
      }
      b1

      // 方式二：map合并
      //      map2.foreach{
      //        case (city, cnt) => {
      //          val newCount = map1.getOrElse(city, 0L) + cnt
      //          map1.update(city, newCount)
      //        }
      //      }
      //      b1.cityMap = map1
      //      b1
    }

    // 将统计的结果生成字符串信息）
    override def finish(buff: Buffer): String = {
      val remarkList = ListBuffer[String]()

      val totalCnt = buff.total
      val cityMap = buff.cityMap

      // 需要按照比例 降序排列，取2个
      val cityCntList = cityMap.toList.sortWith(
        (left, right) => {
          left._2 > right._2
        }
      ).take(2)

      val hasMore = cityMap.size > 2
      var rsum = 0L

      cityCntList.foreach {
        case (city, cnt) => {
          val r = cnt * 100 / totalCnt
          rsum += r
          remarkList.append(s"其他  ${100 - rsum}%")
        }
      }
      if (hasMore) {
        remarkList.append()
      }

      remarkList.mkString(",")

    }

    override def bufferEncoder: Encoder[Buffer] = Encoders.product

    override def outputEncoder: Encoder[String] = Encoders.STRING
  }

}
