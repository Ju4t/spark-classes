package com.ju4t.bigdata.spark.core.rdd.operator.tarnsform

import org.apache.spark.{SparkConf, SparkContext}

object OperatorTransformPartitioner {
  def main(args: Array[String]): Unit = {
    val sparkConf = new SparkConf().setMaster("local[*]").setAppName("Operator")
    val sc = new SparkContext(sparkConf)

    // TODO 算子 - mapPartitions
    val rdd = sc.makeRDD(List(1, 2, 3, 4), 3)

    // mapPartitions：效率高：拿到一个分区数据后再做操作，而不是一个一个拿
    //                以分区为单位进行数据转换操作
    //                但是会将真个分区的数据加载到内存中引用
    //                如果处理万的数据是不会被释放掉的，存在对象的引用
    //                在内存较小，数据量较大的场合下，容易出现内存溢出
    val mapRDD = rdd.mapPartitions(
      iter => {
        // 验证结果 >>>>>>> 只打印李两次，即 有多少分区，执行多少次
        println(">>>>>>>")
        iter.map(_ * 2) // 内存操作
      }
    )



    mapRDD.collect().foreach(println)

    sc.stop()
  } 
}
