package com.ju4t.bigdata.spark.sql

import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession

object Spark02_SparkSQL_UDF {
  def main(args: Array[String]): Unit = {
    // TODO 创建SparkSQL的运行环境
    val sparkConf = new SparkConf().setMaster("local[*]").setAppName("sparkSQL")
    val spark = SparkSession.builder().config(sparkConf).getOrCreate()
    // 隐式转换 spark 是上面的定义的对象，不是包名
    import spark.implicits._

    // TODO 执行逻辑

    val df = spark.read.json("data/json/user.json")
    df.createOrReplaceTempView("user")

    spark.sql("select age, username from user").show
    //    +---+---------+
    //    |age| username|
    //    +---+---------+
    //    | 40|zhangshan|
    //    | 30|     lisi|
    //    | 20|   wangwu|
    //    +---+---------+

    // 需求：名称加前缀
    spark.udf.register("prefixName", (name: String) => {
      "Name：" + name
    })
    spark.sql("select age, prefixName(username) from user").show
    //    +---+--------------------+
    //    |age|prefixName(username)|
    //    +---+--------------------+
    //    | 40|     Name：zhangshan|
    //    | 30|          Name：lisi|
    //    | 20|        Name：wangwu|
    //    +---+--------------------+

    // TODO 关闭
    spark.close()

  }
}
