package com.ju4t.bigdata.spark.core.framework.common

import com.ju4t.bigdata.spark.core.framework.util.EnvUtil
import org.apache.spark.{SparkConf, SparkContext}

trait TraitApplication {
  def start(master: String = "local[*]", app: String = "Application")(op: => Unit): Unit = {

    val sparkConf: SparkConf = new SparkConf().setMaster(master).setAppName(app)
    val sc = new SparkContext(sparkConf)
    // 忘线程里放数据
    EnvUtil.put(sc)

    try {
      op
    } catch {
      case ex => println(ex.getMessage)
    }


    // TODO 关闭连接
    sc.stop()

    // 清理 sc
    EnvUtil.clear()
  }
}
