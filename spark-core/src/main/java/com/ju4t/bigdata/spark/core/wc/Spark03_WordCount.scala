
package com.ju4t.bigdata.spark.core.wc

import org.apache.spark.{SparkConf, SparkContext}
import scala.collection.mutable

object Spark03_WordCount {
  def main(args: Array[String]): Unit = {
    // TODO 建立和Spark框架的连接
    val sparkConf = new SparkConf().setMaster("local[*]").setAppName("WordCount")
    val sc = new SparkContext(sparkConf)

    // TODO
    wordcount91011(sc)

    // TODO 关闭连接
    sc.stop()
  }

  // groupBy
  def wordcount1(sc: SparkContext): Unit = {
    val rdd = sc.makeRDD(List("Hello Scala", "Hello Spark"))
    val words = rdd.flatMap(_.split(" "))
    val group = words.groupBy(word => word)
    val wordCount = group.mapValues(iter => iter.size)
  }

  // groupByKey：有shuffle过程，效率不高
  def wordcount2(sc: SparkContext): Unit = {
    val rdd = sc.makeRDD(List("Hello Scala", "Hello Spark"))
    val words = rdd.flatMap(_.split(" "))
    val wordOne = words.map((_, 1))
    val group = wordOne.groupByKey()
    val wordCount = group.mapValues(iter => iter.size)
  }

  // reduceByKey
  def wordcount3(sc: SparkContext): Unit = {
    val rdd = sc.makeRDD(List("Hello Scala", "Hello Spark"))
    val words = rdd.flatMap(_.split(" "))
    val wordOne = words.map((_, 1))
    val wordCount = wordOne.reduceByKey(_ + _)
  }

  // aggregateByKey
  def wordcount4(sc: SparkContext): Unit = {
    val rdd = sc.makeRDD(List("Hello Scala", "Hello Spark"))
    val words = rdd.flatMap(_.split(" "))
    val wordOne = words.map((_, 1))
    val wordCount = wordOne.aggregateByKey(0)(_ + _, _ + _)
  }

  // foldByKey
  def wordcount5(sc: SparkContext): Unit = {
    val rdd = sc.makeRDD(List("Hello Scala", "Hello Spark"))
    val words = rdd.flatMap(_.split(" "))
    val wordOne = words.map((_, 1))
    val wordCount = wordOne.foldByKey(0)(_ + _)
  }

  // combineByKey
  def wordcount6(sc: SparkContext): Unit = {
    val rdd = sc.makeRDD(List("Hello Scala", "Hello Spark"))
    val words = rdd.flatMap(_.split(" "))
    val wordOne = words.map((_, 1))
    val wordCount = wordOne.combineByKey(
      v => v,
      (x: Int, y: Int) => x + y,
      (x: Int, y: Int) => x + y
    )
  }

  // countByKey
  def wordcount7(sc: SparkContext): Unit = {
    val rdd = sc.makeRDD(List("Hello Scala", "Hello Spark"))
    val words = rdd.flatMap(_.split(" "))
    val wordOne = words.map((_, 1))
    val wordCount = wordOne.countByKey()
  }

  // countByValue
  def wordcount8(sc: SparkContext): Unit = {
    val rdd = sc.makeRDD(List("Hello Scala", "Hello Spark"))
    val words = rdd.flatMap(_.split(" "))
    val wordCount = words.countByValue()
  }

  // reduce, aggregate, fold
  def wordcount91011(sc: SparkContext): Unit = {
    val rdd = sc.makeRDD(List("Hello Scala", "Hello Spark"))
    val words = rdd.flatMap(_.split(" "))

    // 【(word, count),(word, count)】
    //  把word => map[word,1]
    val mapWord = words.map(
      word => {
        mutable.Map[String, Long]((word, 1))
      }
    )

    val wordCount = mapWord.reduce(
      (map1, map2) => {
        map2.foreach {
          case (word, count) => {
            val newCount = map1.getOrElse(word, 0L) + count
            map1.update(word, newCount)
          }
        }
        map1
      }
    )

  }


}
