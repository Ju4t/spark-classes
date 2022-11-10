package com.ju4t.bigdata.spark.streaming

import com.ju4t.bigdata.spark.util.JDBCUtil
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.spark.SparkConf
import org.apache.spark.streaming.kafka010.{ConsumerStrategies, KafkaUtils, LocationStrategies}
import org.apache.spark.streaming.{Seconds, StreamingContext}

import java.text.SimpleDateFormat
import java.util.Date
import scala.collection.mutable.ListBuffer

object SparkStreaming12_Req2_AdCount {

  def main(args: Array[String]): Unit = {

    val sparkConf = new SparkConf().setMaster("local[*]").setAppName("SparkStreaming")
    val ssc = new StreamingContext(sparkConf, Seconds(3))

    val broker = "localhost:9092"
    val groupId = "test-consumer-group"
    val topic = "topic"

    val kafkaParams: Map[String, Object] = Map[String, Object](
      ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG -> broker,
      ConsumerConfig.GROUP_ID_CONFIG -> groupId, // GROUP_ID_CONFIG
      "key.deserializer" -> "org.apache.kafka.common.serialization.StringDeserializer",
      "value.deserializer" -> "org.apache.kafka.common.serialization.StringDeserializer"
    )

    val kafkaDataDS = KafkaUtils.createDirectStream[String, String](
      ssc,
      LocationStrategies.PreferConsistent, // 我们的采集节点和计算的节点该如何匹配，类似于首选位置
      ConsumerStrategies.Subscribe[String, String](Set(topic), kafkaParams)
    )

    val adClickData = kafkaDataDS.map(
      kafkaData => {
        val data = kafkaData.value()
        val datas = data.split(" ")
        AdClickData(datas(0), datas(1), datas(2), datas(3), datas(4))
      }
    )
    val ds = adClickData.transform(
      rdd => {

        // TODO 通过 JDBC 周期性获取黑名单数据
        val blackList = ListBuffer[String]()
        val conn = JDBCUtil.getConnection
        val pstat = conn.prepareStatement("select userid from black_list")
        val rs = pstat.executeQuery()

        while (rs.next()) {
          blackList.append(rs.getString(1))
        }

        rs.close()
        pstat.close()
        conn.close()

        // TODO 判断点击用户是否在黑名单中
        val filterRDD = rdd.filter(
          data => {
            !blackList.contains(data.user)
          }
        )

        // TODO 如果用户不在黑名单，那么进行统计 点击数量（每个采集周期）
        filterRDD.map(
          data => {
            val sdf = new SimpleDateFormat("yyyy-MM-dd")
            val day = sdf.format(new Date(data.ts.toLong))
            val user = data.user
            val ad = data.ad
            ((day, user, ad), 1) // (word, count)
            // key => day+user+ad
            // value => count
          }
        ).reduceByKey(_ + _)
      }
    )

    ds.foreachRDD(
      rdd => {
        /*
            rdd.foreach 方法会每一条数据创建连接
            foreach 方式是RDD算子，算子之外的代码是在Driver执行，算子内的代码是在Executor执行
            这样就会射击闭包操作，Driver端的数据需要传递到Executor端，需要将数据进行序列化
            数据库的 连接对象 是不能序列化的

            RDD提供了一个算子，可以有效的提升效率
            rdd.foreachPartition
            可以一个分区创建一个连接对象，这样可以大幅度减少连接对象的数量，提升效率
         */

        rdd.foreachPartition(
          iter => {
            // 以一个分区为单位 来创建连接对象
            val conn = JDBCUtil.getConnection
            iter.foreach {
              case ((day, user, ad), count) => {
                print(s"${day} ${user} ${ad} ${count} \n")
                if (count >= 30) {
                  // TODO 如果统计数量超过点击阈值(30)，那么将用户拉入到黑名单
                  val conn = JDBCUtil.getConnection
                  val sql =
                    """
                      | INSERT INTO black_list (userid) VALUES (?)
                      | ON DUPLICATE KEY
                      | UPDATE userid = ?
                      |""".stripMargin
                  JDBCUtil.executeUpdate(conn, sql, Array(user, user))
                  conn.close()
                } else {
                  val conn = JDBCUtil.getConnection
                  val sql =
                    """
                      | SELECT * FROM user_ad_count WHERE dt = ? AND userid = ? AND adid = ?
                      |""".stripMargin
                  val flag = JDBCUtil.isExist(conn, sql, Array(day, user, ad))

                  // 查询统计表数据
                  if (flag) {
                    // 如果存在，那么更新
                    val sql1 =
                      """
                        | UPDATE user_ad_count
                        | SET count = count + ?
                        | WHERE dt = ? AND userid = ? AND adid = ?
                        |""".stripMargin
                    JDBCUtil.executeUpdate(conn, sql1, Array(count, day, user, ad))

                    // TODO 如果没有超过阈值，那额需要将当天的广告点击数量进行更新

                    val sql2 =
                      """
                        | SELECT * FROM user_ad_count
                        | WHERE dt = ? AND userid = ? AND adid = ? AND count >=300000
                        |""".stripMargin
                    val flag1 = JDBCUtil.isExist(conn, sql2, Array(day, user, ad))

                    if (flag1) {
                      val sql3 =
                        """
                          | INSERT INTO black_list (userid) VALUES (?)
                          | ON DUPLICATE KEY
                          | UPDATE userid = ?
                          |""".stripMargin
                      JDBCUtil.executeUpdate(conn, sql3, Array(user, user))
                    }
                  } else {
                    // 如果不存在，那么新增
                    val sql4 =
                      """
                        | INSERT INTO user_ad_count (dt, userid, adid, count) VALUES (?,?,?,?)
                        |""".stripMargin
                    JDBCUtil.executeUpdate(conn, sql4, Array(day, user, ad, count))
                  }
                  conn.close()
                }
              }
            }
            conn.close()
          }
        )

      }
    )

    ssc.start()
    ssc.awaitTermination()
  }

  // 广告数据样例类
  case class AdClickData(ts: String, area: String, city: String, user: String, ad: String)

}

