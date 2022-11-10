package com.ju4t.bigdata.spark.sql

import org.apache.spark.SparkConf
import org.apache.spark.sql.expressions.Aggregator
import org.apache.spark.sql.{Encoder, Encoders, SaveMode, SparkSession}

object Spark04_SparkSQL_JDBC {
  def main(args: Array[String]): Unit = {

    // TODO 创建SparkSQL的运行环境

    val sparkConf = new SparkConf().setMaster("local[*]").setAppName("sparkSQL")
    val spark = SparkSession.builder().config(sparkConf).getOrCreate()
    // 隐式转换 spark 是上面的定义的对象，不是包名
    import spark.implicits._

    // 读取MySQL数据
    val df = spark.read
      .format("jdbc")
      .option("url", "jdbc:mysql://192.168.8.3:3306/movie?useSSL=false") // movie 数据库
      .option("driver", "com.mysql.cj.jdbc.Driver")
      .option("user", "root")
      .option("password", "123456")
      .option("dbtable", "subject_item") // 表名
      .load()

    // 读取，默认只读20条数据
    df.show()

    // 保存数据 把上面df的保存到另一张表
    // 数据库里多了张 subject_item_NEW 表
//    df.write
//      .format("jdbc")
//      .option("url", "jdbc:mysql://192.168.8.3:3306/movie?useSSL=false") // movie 数据库
//      .option("driver", "com.mysql.cj.jdbc.Driver")
//      .option("user", "root")
//      .option("password", "123456")
//      .option("dbtable", "subject_item_NEW") // 表名
//      .mode(SaveMode.Append)
//      .save()

    // TODO 关闭
    spark.close()
  }

}
