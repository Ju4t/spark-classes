package com.ju4t.bigdata.spark.core.framework.service

import com.ju4t.bigdata.spark.core.framework.common.TraitService
import com.ju4t.bigdata.spark.core.framework.dao.WordCountDao
import org.apache.spark.rdd.RDD

/*
    服务层
 */
class WordCountService extends TraitService{
  private val wordCountDao = new WordCountDao()

  // 数据分析
  def dataAnalysis() = {

    // TODO 执行业务操作
    val lines = wordCountDao.readFile("data/WordCount")

    val words: RDD[String] = lines.flatMap(_.split(" "))

    val wordToOne = words.map(
      word => (word, 1)
    )

    val wordGroup: RDD[(String, Iterable[(String, Int)])] = wordToOne.groupBy(
      t => t._1
    )

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

    // 返回结果
    array
  }
}
