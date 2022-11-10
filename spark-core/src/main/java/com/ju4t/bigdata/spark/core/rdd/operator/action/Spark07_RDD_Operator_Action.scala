package com.ju4t.bigdata.spark.core.rdd.operator.action

import org.apache.spark.{SparkConf, SparkContext}

object Spark07_RDD_Operator_Action {
  def main(args: Array[String]): Unit = {
    val sparkConf = new SparkConf().setMaster("local[*]").setAppName("Operator")
    val sc = new SparkContext(sparkConf)

    val rdd = sc.makeRDD(List(1, 2, 3, 4))

    val user = new User()

    // Task not serializable
    // NotSerializableException: com.ju4t.bigdata.spark.core.rdd.operator.action.Spark07_RDD_Operator_Action$User
    rdd.foreach(
      num => {
        println("age=" + (user.age + num))
      }
    )
    //    age=32
    //    age=33
    //    age=34
    //    age=31

    sc.stop()
  }

  //  class User extends Serializable {
  //  样例类在编译时，会自动混入序列化特质（实现可序列化接口）
  case class User() {
    var age: Int = 30
  }

}
