package com.ju4t.bigdata.spark.util

import com.alibaba.druid.pool.DruidDataSourceFactory
import groovy.sql.Sql
import org.apache.hadoop.yarn.webapp.Params

import java.sql.{Connection, PreparedStatement}
import java.util.Properties
import javax.sql.DataSource

object JDBCUtil {
  // 初始化连接池
  var dataSource: DataSource = init()

  def init(): DataSource = {
    val properties = new Properties()
    properties.setProperty("driverClassName", "com.mysql.cj.jdbc.Driver")
    properties.setProperty("url", "jdbc:mysql://192.168.8.3:3306/spark-streaming?useSSL=false")
    properties.setProperty("username", "root")
    properties.setProperty("password", "123456")
    properties.setProperty("maxActive", "50")
    DruidDataSourceFactory.createDataSource(properties)
  }

  // 获取MySQL连接
  def getConnection: Connection = {
    dataSource.getConnection
  }

  // 执行SQL语句，单条数据插入
  def executeUpdate(connection: Connection, sql: String, params: Array[Any]): Int = {
    var rtn = 0
    var pstms: PreparedStatement = null
    try {
      connection.setAutoCommit(false)
      pstms = connection prepareStatement (sql)
      if (params != null && params.length > 0) {
        for (i <- params.indices) {
          pstms.setObject(i + 1, params(i))
        }
      }
      rtn = pstms.executeUpdate()
      connection.commit()
      pstms.close()
    } catch {
      case e: Exception => e.printStackTrace()
    }
    rtn
  }

  // 判断一条数据是否存在
  def isExist(connection: Connection, sql: String, params: Array[Any]): Boolean = {
    var flag: Boolean = false
    var psmt: PreparedStatement = null
    try {
      psmt = connection.prepareStatement(sql)
      for (i <- params.indices) {
        psmt.setObject(i + 1, params(i))
      }
      flag = psmt.executeQuery().next()
    }
    catch {
      case e: Exception => e.printStackTrace()
    }
    flag
  }

  // 获取MySQL的一条数据

}
