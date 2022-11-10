package com.ju4t.bigdata.spark.sql

import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession

object Spark05_SparkSQL_HIVE {
  def main(args: Array[String]): Unit = {

    // TODO 创建SparkSQL的运行环境

    // 如果出现没有权限的话，加上如下内容
    // System.setProperty("HADOOP_USER_NAME", "root")

    val sparkConf = new SparkConf().setMaster("local[*]").setAppName("sparkSQL")
    val spark = SparkSession
      .builder()
      .enableHiveSupport()
      .config(sparkConf)
      .getOrCreate()

    // 使用SparkSQL连接外部的Hive
    // 1. 将hive-site.xml 复制到 项目的 resources 目录里
    // 2. 启用hive的支持 .enableHiveSupport()
    // 3. 增加对应的依赖关系（包含MySQL驱动）
    spark.sql("show tables").show
    // 如果连接的不是外部的hive，检查target/classes/hive-site.xml 是否存在，或者复制一份进去
    

    // TODO 关闭
    spark.close()
  }

}
