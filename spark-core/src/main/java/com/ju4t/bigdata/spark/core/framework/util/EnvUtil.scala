package com.ju4t.bigdata.spark.core.framework.util

import org.apache.spark.SparkContext

// 工具类
object EnvUtil {
  private val scLocal = new ThreadLocal[SparkContext]

  // 忘线程里放数据
  def put(sc: SparkContext): Unit = {
    scLocal.set(sc)
  }

  //
  def take(): SparkContext = {
    scLocal.get()
  }

  // 清理线程数据
  def clear(): Unit = {
    scLocal.remove()
  }
}
