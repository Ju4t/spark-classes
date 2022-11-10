package com.ju4t.bigdata.spark.sql

import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession

object Spark01_SparkSQL_Basic {
  def main(args: Array[String]): Unit = {
    // TODO 创建SparkSQL的运行环境
    val sparkConf = new SparkConf().setMaster("local[*]").setAppName("sparkSQL")
    val spark = SparkSession.builder().config(sparkConf).getOrCreate()
    // 隐式转换
    import spark.implicits._

    // TODO 执行逻辑

    // TODO DataFrame

    //    val df = spark.read.json("data/json/user.json")
    //    df.show

    // DataFrame => SQL
    //    df.createOrReplaceTempView("user")
    //    spark.sql("select age, username from user").show

    // DataFrame => DSL
    //    df.select("age", "username").show

    // 在使用DataFrame，如果涉及到转换操作，需要引入转换规则
    // 下面的spark不是包名，而是上面的构建的对象名
    //    import spark.implicits._
    //    df.select($"age" + 1).show
    //    df.select('age + 1).show

    // TODO DataSet

    // DataFrame其实是特定泛型的DataSet
    //    val seq = Seq(1, 2, 3, 4)
    //    val ds = seq.toDS()
    //    ds.show()

    // RDD <=> DataFrame
    //    // =>
    //    val rdd = spark.sparkContext.makeRDD(List(
    //      (1, "zhangshan", 30),
    //      (2, "lisi", 40),
    //      (3, "wangwu", 20)
    //    ))
    //    val df = rdd.toDF("id", "name", "age")
    //    // <=
    //    val rowRDD = df.rdd

    // DataFrame <=> DataSet
    //    // =>
    //    val ds = df.as[User]
    //    // <=
    //    val df1 = ds.toDF()

    // RDD <=> DataSet
    val rdd = spark.sparkContext.makeRDD(List(
      (1, "zhangshan", 30),
      (2, "lisi", 40),
      (3, "wangwu", 20)
    ))
    // =>
    val ds1 = rdd.map {
      case (id, name, age) => {
        User(id, name, age)
      }
    }.toDS()
    // =>
    val userRDD = ds1.rdd

    // TODO 关闭
    spark.close()

  }

  case class User(id: Int, name: String, age: Int)

}
