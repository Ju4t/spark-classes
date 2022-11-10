package com.ju4t.bigdata.spark.core.req

import org.apache.spark.util.AccumulatorV2
import org.apache.spark.{SparkConf, SparkContext}

import scala.collection.mutable

object Spark04_Req1_HotCategoryTop10Analysis {
  def main(args: Array[String]): Unit = {
    val sparkConf = new SparkConf().setMaster("local[*]").setAppName("HotCategoryTop10Analysis")
    val sc = new SparkContext(sparkConf)

    // TODO 完美
    // Q：依然存在shuffle操作（reduceByKey）
    //    彻底不要有shuffle，用 累加器



    // 1. 读取原始日志
    val bhvRDD = sc.textFile("data/bhv/")

    val acc = new HotCategoryAccumulator
    sc.register(acc, "HotCategory")

    // 2. 将我们的数据转换结构
    //    点击的场合: (品类ID, (1, 0，0))
    //    加车的场合: (品类ID, (0, 1，0))
    //    支付的场合: (品类ID, (0, 0，1))
    val flatRDD = bhvRDD.foreach(
      bhv => {
        val datas = bhv.split("------")
        if (datas(5) == "click") {
          acc.add(datas(2), "click")
        } else if (datas(5) == "cart") {
          acc.add(datas(2), "cart")
        } else if (datas(5) == "buy") {
          acc.add(datas(2), "buy")
        } else {
          Nil
        }
      }
    )

    val accValue = acc.value
    val categories = accValue.map(_._2)

    // 排序
    val sort = categories.toList.sortWith(
      (left, right) => {
        if (left.clickCnt > right.clickCnt) {
          true
        } else if (left.clickCnt == right.clickCnt) {
          if (left.cartCnt > right.cartCnt) {
            true
          } else {
            left.buyCnt > right.buyCnt
          }
        } else {
          false
        }
      }
    )

    // 4. 将结果采集到控制台打印出来，上面已经take了，所以下面不需要在collect
    sort.take(10).foreach(println)
    sc.stop()
  }

  case class HotCategory(cid: String, var clickCnt: Int, var cartCnt: Int, var buyCnt: Int)

  /*
    自定义累加器
    1. 继承AccumulatorV2，定义泛型
        IN: (品类ID，行为类型)
        OUT: mutable.Map[String, HotCategory]
    2. 重写方法
   */
  class HotCategoryAccumulator extends AccumulatorV2[(String, String), mutable.Map[String, HotCategory]] {
    private val hcMap = mutable.Map[String, HotCategory]()

    override def isZero: Boolean = {
      hcMap.isEmpty
    }

    override def copy(): AccumulatorV2[(String, String), mutable.Map[String, HotCategory]] = {
      new HotCategoryAccumulator()
    }

    override def reset(): Unit = {
      hcMap.clear()
    }

    override def add(v: (String, String)): Unit = {
      val cid = v._1
      val bhv = v._2
      val category = hcMap.getOrElse(cid, HotCategory(cid, 0, 0, 0))

      if (bhv == "click") {
        category.clickCnt += 1
      } else if (bhv == "cart") {
        category.cartCnt += 1
      } else if (bhv == "buy") {
        category.buyCnt += 1
      }
      hcMap.update(cid, category)
    }

    override def merge(other: AccumulatorV2[(String, String), mutable.Map[String, HotCategory]]): Unit = {
      val map1 = this.hcMap
      val map2 = other.value
      map2.foreach {
        case (cid, hc) => {
          val category = map1.getOrElse(cid, HotCategory(cid, 0, 0, 0))
          category.clickCnt += hc.clickCnt
          category.cartCnt += hc.cartCnt
          category.buyCnt += hc.buyCnt
          map1.update(cid, category)
        }
      }
    }

    override def value: mutable.Map[String, HotCategory] = hcMap
  }

}
