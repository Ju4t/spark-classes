package com.ju4t.bigdata.spark.sql

import org.apache.hadoop.mapred.lib.aggregate.UserDefinedValueAggregatorDescriptor
import org.apache.parquet.filter2.predicate.Operators.UserDefined
import org.apache.spark.SparkConf
import org.apache.spark.sql.{Row, SparkSession}
import org.apache.spark.sql.expressions.{MutableAggregationBuffer, UserDefinedAggregateFunction}
import org.apache.spark.sql.types.{DataType, LongType, StructField, StructType}

object Spark03_SparkSQL_UDAF {
  def main(args: Array[String]): Unit = {
    // TODO 创建SparkSQL的运行环境
    val sparkConf = new SparkConf().setMaster("local[*]").setAppName("sparkSQL")
    val spark = SparkSession.builder().config(sparkConf).getOrCreate()
    // 隐式转换 spark 是上面的定义的对象，不是包名
    import spark.implicits._

    // TODO 执行逻辑

    val df = spark.read.json("data/json/user.json")
    df.createOrReplaceTempView("user")

    // 需求：
    spark.udf.register("ageAvg", new MyAvgUDAF())
    spark.sql("select ageAvg(age) from user").show
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
  UserDefinedAggregateFunction 横线，仅演示
  1. 继承 UserDefinedAggregateFunction
  2. 重写方法（8个）
   */
  class MyAvgUDAF extends UserDefinedAggregateFunction {
    // 输入数据的结构，IN
    override def inputSchema: StructType = {
      StructType(
        Array(
          StructField("age", LongType)
        )
      )
    }

    // 缓存区 数据的结构 buffer
    override def bufferSchema: StructType = {
      StructType(
        Array(
          StructField("total", LongType),
          StructField("count", LongType)
        )
      )
    }

    // 函数计算结果的类型 Out
    override def dataType: DataType = LongType

    // 函数的稳定性
    override def deterministic: Boolean = true

    // 缓冲区初始化
    override def initialize(buffer: MutableAggregationBuffer): Unit = {
      // 0,1 表示结构中的位置
      //      buffer(0) = 0L
      //      buffer(1) = 0L

      buffer.update(0, 0L)
      buffer.update(1, 0L)
    }

    // 根据输入的值来更细缓冲区数据
    override def update(buffer: MutableAggregationBuffer, input: Row): Unit = {

      // buffer 有 total 和 count
      // buffer.getLong(0) = total // 缓冲区旧的值
      // buffer.getLong(1) = count // 缓冲区旧的值

      // input 只有 age
      // input.getLong(0) = age

      buffer.update(0, buffer.getLong(0) + input.getLong(0))
      buffer.update(1, buffer.getLong(1) + 1)
    }

    // 缓冲区数据合并，分布式计算是有个缓冲区的，所以需要合并
    override def merge(buffer1: MutableAggregationBuffer, buffer2: Row): Unit = {
      // 不断的更新 buffer1
      buffer1.update(0, buffer1.getLong(0) + buffer2.getLong(0))
      buffer1.update(1, buffer1.getLong(1) + buffer2.getLong(1))
    }

    // 计算：平均值
    override def evaluate(buffer: Row): Any = {
      buffer.getLong(0)/buffer.getLong(1)
    }
  }

}
