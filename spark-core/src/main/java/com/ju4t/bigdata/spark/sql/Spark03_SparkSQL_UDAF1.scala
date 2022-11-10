package com.ju4t.bigdata.spark.sql

import org.apache.spark.SparkConf
import org.apache.spark.sql.{Encoder, Encoders, SparkSession, functions}
import org.apache.spark.sql.expressions.Aggregator

object Spark03_SparkSQL_UDAF1 {
  def main(args: Array[String]): Unit = {

    // TODO 创建SparkSQL的运行环境

    val sparkConf = new SparkConf().setMaster("local[*]").setAppName("sparkSQL")
    val spark = SparkSession.builder().config(sparkConf).getOrCreate()

    // TODO 执行逻辑

    val df = spark.read.json("data/json/user.json")
    df.createOrReplaceTempView("user")

    // 需求：求平均值（强类型）
    spark.udf.register("ageAvg", functions.udaf(new MyAvgUDAF()))
    spark.sql("select ageAvg(age) from user").show
    // 执行预期结果如下
    //    +-----------+
    //    |ageavg(age)|
    //    +-----------+
    //    |         30|
    //    +-----------+

    // TODO 关闭
    spark.close()
  }

  /*
  自定义聚合函数类：计算年龄的平均值
  1. 继承 import org.apache.spark.sql.expressions.Aggregator，定义泛型
      IN：数据的数据类型Long
      BUF：case class Buff(var total: Long, var count: Long)
      OUT：输出的数据类型Long
  2. 重写方法（4+2个）
   */

  // BUF 样例类
  case class Buff(var total: Long, var count: Long)

  class MyAvgUDAF extends Aggregator[Long, Buff, Long] {
    // 一般在scala中，z & zero 初始值，零值
    // 缓冲区的初始化
    override def zero: Buff = {
      Buff(0L, 0L)
    }

    // 根据输入的数据来跟新缓冲区的数据
    override def reduce(buff: Buff, in: Long): Buff = {
      buff.total = buff.total + in
      buff.count = buff.count + 1
      // 返回
      buff
    }

    // 合并缓冲区的
    override def merge(b1: Buff, b2: Buff): Buff = {
      b1.total = b1.total + b2.total
      b1.count = b1.count + b2.count
      b1
    }

    // 计算结果
    override def finish(buff: Buff): Long = {
      buff.total / buff.count
    }

    // 分布式 数据需要在网络中传输
    // 缓冲区序列化编码操作
    // Encoders.product 固定写法
    override def bufferEncoder: Encoder[Buff] = Encoders.product

    // 缓冲区编码操作
    // Encoders.scalaLong 固定写法
    override def outputEncoder: Encoder[Long] = Encoders.scalaLong
  }

}
