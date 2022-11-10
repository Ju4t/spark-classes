package com.ju4t.bigdata.spark.core.rdd.part

import org.apache.spark.{HashPartitioner, Partitioner, SparkConf, SparkContext}

object Spark01_RDD_Part {
  def main(args: Array[String]): Unit = {
    val sparkConf = new SparkConf().setMaster("local[*]").setAppName("Part")
    val sc = new SparkContext(sparkConf)

    // TODO
    // 需求：把nba、cba、wba分别放到一个分区

    val rdd = sc.makeRDD(List(
      ("nba", "xxxxxxxxxxxxxxx"),
      ("cba", "xxxxxxxxxxxxxxx"),
      ("nba", "xxxxxxxxxxxxxxx"),
      ("wba", "xxxxxxxxxxxxxxx"),
      ("nba", "xxxxxxxxxxxxxxx")
    ))

    val partRDD = rdd.partitionBy(new MyPartitioner)

    partRDD.saveAsTextFile("output")

    sc.stop()

  }

  /*
    自定义分区，参考 new HashPartitioner()
    1. 继承 Partitioner
    2. 重写 方法
   */
  class MyPartitioner extends Partitioner {
    // 分区数量
    override def numPartitions: Int = 3

    // 根据数据的key值返回数据所在的分区索引（从0开始）
    override def getPartition(key: Any): Int = {
      key match {
        case "nba" => 0
        case "wba" => 1
        case _ => 2
      }

    }
  }

}
