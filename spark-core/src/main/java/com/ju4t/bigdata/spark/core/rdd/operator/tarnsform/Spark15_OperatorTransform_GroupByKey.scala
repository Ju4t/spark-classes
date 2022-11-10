package com.ju4t.bigdata.spark.core.rdd.operator.tarnsform

import org.apache.spark.{SparkConf, SparkContext}

object Spark15_OperatorTransform_GroupByKey {
  def main(args: Array[String]): Unit = {
    val sparkConf = new SparkConf().setMaster("local[*]").setAppName("Operator")
    val sc = new SparkContext(sparkConf)

    val rdd = sc.makeRDD(List(
      ("a", 1),
      ("a", 2),
      ("a", 3),
      ("b", 4)
    ))

    // TUDO 算子 （Key - Value 类型）
    // groupByKey：讲数据源中的数据，相同key的数据分在一个组中，形成一个对偶元组
    //            元组中的第一个元素就是key
    //            元组中的第二个元素就是相同的value的集合
    val groupRDD = rdd.groupByKey()

    groupRDD.collect().foreach(println)
    //    (a,CompactBuffer(1, 2, 3))
    //    (b,CompactBuffer(4))

    // 指定key，结果和上面一样
    val groupRDD1 = rdd.groupBy(_._1)
    groupRDD1.collect().foreach(println)
    //    (a,CompactBuffer((a,1), (a,2), (a,3)))
    //    (b,CompactBuffer((b,4)))

    // 关闭
    sc.stop()

  }
}
