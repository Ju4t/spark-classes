package com.ju4t.bigdata.spark.core.framework.application

import com.ju4t.bigdata.spark.core.framework.common.TraitApplication
import com.ju4t.bigdata.spark.core.framework.controller.WordCountController

// extends App 或者 main 方法
object WordCountApplication extends App with TraitApplication {

  // 启动应用程序
  start("local[*]", "WordCount"){
    // 变化的逻辑传递到 TraitApplication
    val controller = new WordCountController()
    controller.execute()
  }

}
