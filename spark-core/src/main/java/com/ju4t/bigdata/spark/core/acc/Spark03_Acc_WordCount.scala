package com.ju4t.bigdata.spark.core.acc

import org.apache.spark.util.AccumulatorV2
import org.apache.spark.{SPARK_BRANCH, SparkConf, SparkContext}

import scala.collection.mutable

object Spark03_Acc_WordCount {
  def main(args: Array[String]): Unit = {
    val sparkConf = new SparkConf().setMaster("local[*]").setAppName("ACC")
    val sc = new SparkContext(sparkConf)

    // TODO 自定义累加器
    val rdd = sc.makeRDD(List(
      "Hello Spark", "Hello Scala"
    ))

    // 方式一：worcount 带shuffle操作
    // rdd.flatMap(_.split(" ")).map((_,1)).reduceByKey(_+_).foreach(println)

    // 方式二：累加器，不带shuffle操作
    // 创建累加器对象
    val wcAcc = new MyAccumulator()

    // 向Spark注册
    sc.register(wcAcc, "worCountAcc")

    rdd.flatMap(_.split(" ")).foreach(
      word => {
        // 数据的累加
        wcAcc.add(word)
      }
    )

    // 获取累加器累加的结果
    println(wcAcc.value)

    sc.stop()
  }

  /*
    自定义数据累加器：WordCount

    1. 继承 AccumulatorV2
       IN: 累加器输入的数据类型 String
       OUT: 累加器返回的数据类型 mutable.Map[String, Long]
    2. 重写方法（6个）

   */
  class MyAccumulator extends AccumulatorV2[String, mutable.Map[String, Long]] {

    // 准备一个空的集合
    private var wcMap = mutable.Map[String, Long]()

    // isZero 判断是否为初始状态
    override def isZero: Boolean = {
      // 就是判断 wcMap 是否为空
      wcMap.isEmpty
    }

    // copy
    override def copy(): AccumulatorV2[String, mutable.Map[String, Long]] = {
      new MyAccumulator
    }

    // 重置累加器
    override def reset(): Unit = {
      wcMap.clear()
    }

    // 获取累加器需要计算的值
    override def add(word: String): Unit = {
      val newCnt = wcMap.getOrElse(word, 0L) + 1
      wcMap.update(word, newCnt)
    }

    // Driver合并多个累加器
    override def merge(other: AccumulatorV2[String, mutable.Map[String, Long]]): Unit = {
      // 两个map的合并
      val map1 = this.wcMap
      val map2 = other.value
      map2.foreach {
        case (word, count) => {
          val newCnt = map1.getOrElse(word, 0L) + count
          map1.update(word, newCnt)
        }
      }
    }

    // 累加器结果
    override def value: mutable.Map[String, Long] = {
      wcMap
    }
  }

}
