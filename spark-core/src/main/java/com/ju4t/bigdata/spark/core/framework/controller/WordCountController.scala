package com.ju4t.bigdata.spark.core.framework.controller

import com.ju4t.bigdata.spark.core.framework.common.TraitController
import com.ju4t.bigdata.spark.core.framework.service.WordCountService

/*
    控制层
 */
class WordCountController extends TraitController{
  private val wordCountService = new WordCountService()

  // 调度
  // 重写 TraitController 的 execute 方法
  def execute(): Unit = {
    val array = wordCountService.dataAnalysis()
    array.foreach(println)
  }

}
