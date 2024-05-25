package com.cc

import com.cc.utils.{ScoreStrategy, StringUtil}
import org.apache.spark.sql.{SaveMode, SparkSession}
import org.apache.spark.streaming.dstream.DStream
import org.apache.spark.streaming.kafka010.{ConsumerStrategies, KafkaUtils, LocationStrategies}
import org.apache.spark.streaming.{Seconds, StreamingContext}

import java.util.Properties

/**
 * Created with IntelliJ IDEA.
 * Author: CC
 * E-mail: 203717588@qq.com
 * Date: 2023/3/22
 * Time: 18:41
 * Description:
 */
object MeiTuanWaiMai_City_Score {

  // City维度
  // 业务：实时统计每个城市店铺评分(score)排名前10的店铺信息(city、id、name、category、score)

  def main(args: Array[String]): Unit = {
    // 获取SparkSQL操作对象
    val spark = SparkSession.builder()
      .appName("City_Score").master("local[*]").getOrCreate()
    val sc = spark.sparkContext
    sc.setCheckpointDir("C:\\Users\\cc\\Desktop\\out")
    // 获取实时处理对象，微批处理时间间隔2s
    val ssc = new StreamingContext(sc,Seconds(2))

    // 消费Kafka数据配置
    val confMap = Map(
      ("key.deserializer","org.apache.kafka.common.serialization.StringDeserializer"),
      ("value.deserializer","org.apache.kafka.common.serialization.StringDeserializer"),
      ("bootstrap.servers","bd91:9092,bd92:9092,bd93:9092"),
      ("group.id","g10"),
      ("auto.offset.reset","earliest")
//      ("enable.auto.commit","false")
    )
    // ODS层
    // 读取Kafka数据，获取DStream对象
    val ods_ds = KafkaUtils.createDirectStream[String, String](
      ssc,
      LocationStrategies.PreferConsistent,
      ConsumerStrategies.Subscribe[String, String](
        Iterable("ods_proSrc_log"),
        confMap
      )
    )
    //ds.map(_.value()).print(3)

    // DWD层
    val dwd_ds = ods_ds
      .map(_.value())
      .map(line => {
        val arr = line.split("\t")
        val id = arr(0)
        val city = arr(2)
        val name = arr(3)
        val category = arr(8)
        val score = arr(9).toDouble
        val comment_number = arr(10).toInt
        val month_sales = try StringUtil.getNumber(arr(11),1).toLong catch {case e: Exception => 0L}
        // 模拟评分变化
        val newScore = ScoreStrategy.generator(score)
        // category类别处理
        val newCategory = category.split("\\s+")(0)
        (city,id,name,newCategory,newScore,comment_number,month_sales)
      })

    // DWS层
    val dws_ds:DStream[List[LogInfo]] = dwd_ds
      .map(t => (t._1, LogInfo(t._1, t._2, t._3, t._4, t._5)))
      .repartition(1)
      .groupByKey()
      .map(t => {
        val list = t._2.toList   // 获取元组第二项List(LogInfo)
        val ls = list
          .distinct
          .sortBy(_.score)       // 根据评分排序
          .reverse               // 降序
          .take(10)              // 获取Top10
        ls
      })

    // 连接属性
    val prop = new Properties()
    prop.setProperty("user","root")
    prop.setProperty("password","123456")
    // 导入转换包
    import spark.implicits._
    // ADS层
    dws_ds.foreachRDD(rdd=>{
      val list = rdd.collect().toList.flatten
      val df = sc.makeRDD(list).toDF("city", "id", "name", "category", "score")
      df.write.mode(SaveMode.Overwrite).jdbc(
        "jdbc:mysql://localhost:3306/day0322_bd_pro?serverTimezone=UTC",
        "area_score_top10",
        prop
      )
    })

    // 开启任务
    ssc.start()
    ssc.awaitTermination()

  }

}

case class LogInfo(var city:String,
                   var id:String,
                   var name:String,
                   var category:String,
                   var score:Double
                  )
