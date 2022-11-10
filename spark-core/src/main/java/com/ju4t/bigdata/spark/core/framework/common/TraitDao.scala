package com.ju4t.bigdata.spark.core.framework.common

import com.ju4t.bigdata.spark.core.framework.util.EnvUtil

trait TraitDao {
  def readFile(path: String) = {
    // Q：拿不到sc
    // ThreadLocal 可以对线程的内存进行控制，存储数据，共享数据
    val sc = EnvUtil.take()
    sc.textFile(path)
  }
}
