package com.ju4t.bigdata.spark.core.wc

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object Spark01_WordCount {
  def main(args: Array[String]): Unit = {
    // Application
    // Spark框架
    // TODO 建立和Spark框架的连接
    // JDBC : Connection
    val sparkConf = new SparkConf().setMaster("local").setAppName("WordCount")
    val sc = new SparkContext(sparkConf);

    // TODO 执行业务操作
    // 1.读取文件，获取一行一行的数据
    // hello word
    val lines: RDD[String] = sc.textFile("data/WordCount")

    // 2.将一行数据进行拆分，形成一个一个的效果
    // 扁平化：将整体拆分成个体对操作
    // "hello word" => hello, word
    val words: RDD[String] = lines.flatMap(_.split(" "))

    // 优化
    val wordToOne = words.map(
      word => (word, 1)
    )

    // 3.将数据根据单词分组，便于统计
    // (hello, hello, hello), (word, word, word)
    val wordGroup: RDD[(String, Iterable[(String, Int)])] = wordToOne.groupBy(
      t => t._1
    )

    // 4.对分组后对数据进行转换
    // (hello, hello, hello), (word, word, word)
    // (hello, 3), (word, 2)
    val wordToCount = wordGroup.map {
      case (word, list) => {
        list.reduce(
          (t1, t2) => {
            (t1._1, t1._2 + t2._2)
          }
        )
      }
    }

    // 5.将转换结果采集到控制台打印出来
    val array: Array[(String, Int)] = wordToCount.collect()
    array.foreach(println)

    // TODO 关闭连接
    sc.stop()
  }
}
