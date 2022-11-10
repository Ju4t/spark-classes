package com.ju4t.bigdata.spark.core.req

import org.apache.spark.{SPARK_BRANCH, SparkConf, SparkContext}

object Spark01_Req1_HotCategoryTop10Analysis {
  def main(args: Array[String]): Unit = {
    // TODO 分类排行 Top10

    val sparkConf = new SparkConf().setMaster("local[*]").setAppName("HotCategoryTop10Analysis")
    val sc = new SparkContext(sparkConf)

    // 1. 读取原始日志
    val bhvRDD = sc.textFile("data/bhv/")

    // 2. 统计品类的点击量：（品类ID，点击数量）
    // 2021-12-16_16/12/2021 16:40:19.911735_10178_18729_16/12/2021 16:40:19.911735_click_1
    // 先过滤数据
    val clickRDD = bhvRDD.filter(
      bhv => {
        val datas = bhv.split("------")
        datas(5) == "click"
      }
    )

    // 再统计
    val clickCountRDD = clickRDD.map(
      bhv => {
        val datas = bhv.split("------")
        (datas(2), 1)
      }
    ).reduceByKey(_ + _)
    // clickCountRDD.collect().foreach(println)

    // 3. 统计商品的下单数量：（品类ID，下单数量）--- 加入购物车，因为没有order数据
    val cartRDD = bhvRDD.filter(
      bhv => {
        val datas = bhv.split("------")
        datas(5) == "cart"
      }
    )
    val cartCountRDD = cartRDD.map(
      bhv => {
        val datas = bhv.split("------")
        (datas(2), 1)
      }
    ).reduceByKey(_ + _)

    // 4. 统计品类的：（品类ID，支付数量）
    val buyRDD = bhvRDD.filter(
      bhv => {
        val datas = bhv.split("------")
        datas(5) == "buy"
      }
    )
    val buyCountRDD = buyRDD.map(
      bhv => {
        val datas = bhv.split("------")
        (datas(2), 1)
      }
    ).reduceByKey(_ + _)

    // 5. 将品类进行排序，并且取top10
    //    点击量排序，下单数量排序，支付数量排序
    //    元组排序：先比较第一个，再比较第二个，以此类推
    //    (品类ID，（点击数量，下单数量，支付数量）)
    //    join: 需要两边都有相同的key，有点击不一定有下单，所以join不适合
    //    zip: 也不合适
    //    leftOutJoin: 左边为主的连接，左和右不确定，所以不合适
    //    cogroup: 会在connect + group，没有数据也有分组存在。所以这个适合！！！
    val cogroupRDD =
    clickCountRDD.cogroup(cartCountRDD, buyCountRDD) // 把这三个相同的key连接在一起

    val analysisRDD = cogroupRDD.mapValues {
      case (clickIter, cartIter, buyIter) => {
        var clickCnt = 0
        val iter1 = clickIter.iterator
        if (iter1.hasNext) {
          clickCnt = iter1.next()
        }

        var cartCnt = 0
        val iter2 = cartIter.iterator
        if (iter2.hasNext) {
          cartCnt = iter2.next()
        }

        var buyCnt = 0
        val iter3 = buyIter.iterator
        if (iter3.hasNext) {
          buyCnt = iter3.next()
        }

        (clickCnt, cartCnt, buyCnt)

      }
    }

    val resultRDD = analysisRDD.sortBy(_._2, false).take(10)

    // 6. 将结果采集到控制台打印出来，上面已经take了，所以下面不需要在collect
    resultRDD.foreach(println)


    sc.stop()
  }
}
