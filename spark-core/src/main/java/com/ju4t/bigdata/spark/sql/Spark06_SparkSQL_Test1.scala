package com.ju4t.bigdata.spark.sql

import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession

object Spark06_SparkSQL_Test1 {
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
    // 这里的热门商品是从 点击量 的纬度来看的，计算 各个区域 前三大热门商品，并准备每个在主要城市中的分布比例，超过两个城市用其他显示

    spark.sql(
      """
        |SELECT * FROM (
        |	SELECT *,rank() over(partition by area order by clickCnt DESC)  as rank FROM (
        |		SELECT
        |			area,
        |			product_name,
        |			count( 1 ) AS clickCnt
        |		FROM
        |			(
        |				SELECT
        |					a.*,
        |					p.product_name,
        |					c.city_name,
        |					c.area
        |				FROM
        |					user_visit_action AS a
        |					JOIN product_info AS p ON a.click_product_id = p.product_id
        |					JOIN city_info AS c ON a.city_d = c.city_id
        |				WHERE
        |					a.click_product_id > - 1
        |			) AS t1
        |		GROUP BY
        |			area,
        |		product_name
        |	) as t2
        |) t3 WHERE rank <= 3
        |""".stripMargin)

    // TODO 关闭
    spark.close()
  }

}
