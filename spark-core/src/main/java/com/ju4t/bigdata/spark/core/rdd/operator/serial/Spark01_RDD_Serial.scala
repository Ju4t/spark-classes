package com.ju4t.bigdata.spark.core.rdd.operator.serial

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object Spark01_RDD_Serial {
  def main(args: Array[String]): Unit = {
    val sparkConf = new SparkConf().setMaster("local").setAppName("Serial")
    val sc = new SparkContext(sparkConf)

    val rdd = sc.makeRDD(Array("hello word", "hello spark", "hello scala", "hive", "ju4t"))

    val search: Search = new Search("h")
//    search.getMatch1(rdd).collect().forearch(println)

    sc.stop()
  }

  // 查询对象，想从数据源中查询指定规则的对象
  class Search(query: String) extends Serializable {

    def isMatch(s: String): Boolean = {
      s.contains(query)
    }

    // 函数序列化案列
    def getMatch1(rdd: RDD[String]): AnyRef = {
      rdd.filter(isMatch)
    }

    // 属性序列化案列
    def getMatch2(rdd: RDD[String]): RDD[String] = {
      rdd.filter(x => x.contains(query))
    }
  }

}
