package com.ju4t.bigdata.spark.sql

import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession

object Spark06_SparkSQL_Test {
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

    // TODO 准备数据
    // 表1：user_visit_action 表结构
    spark.sql(
      """
        |CREATE TABLE `user_visit_action` (
        | `date` string,
        | `user_id` bigint,
        | `session_id` string,
        | `page_id` bigint,
        | `action_time` string,
        | `search_keyword` string,
        | `click_category_id` bigint,
        | `click_product_id` bigint,
        | `order_category_ids` string,
        | `order_product_ids` string,
        | `pay_category_ids` string,
        | `pay_product_ids` string,
        | `city_id` bigint
        | ) row format delimited fields terminated by `\t`
        |""".stripMargin)
    spark.sql(
      """
        |load data local inpath 'data/user_visit_action.txt' into table spark.user_visit_action
        |""".stripMargin)

    // 表2：product_info 表结构
    spark.sql(
      """
        |CREATE TABLE `product_info` (
        | `product_id` bigint,
        | `product_name` string,
        | `extend_info` string
        |) row format delimited fields terminated by `\t`
        |""".stripMargin)
    spark.sql(
      """
        |load data local inpath 'data/product_info.txt' into table spark.product_info
        |""".stripMargin)

    // 表3：city_info 表结构
    spark.sql(
      """
        |CREATE TABLE `city_info` (
        | `city_id` bigint,
        | `city_name` string,
        | `area` string
        |) row format delimited fields terminated by `\t`
        |""".stripMargin)
    spark.sql(
      """
        |load data local inpath 'data/city_info.txt' into table spark.city_info
        |""".stripMargin)

    // 验证
    spark.sql("""select * from city_info""").show

    // TODO 关闭
    spark.close()
  }

}
