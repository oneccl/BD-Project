package com.cc

import com.cc.utils.{ScoreStrategy, StringUtil}
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{SaveMode, SparkSession}
import org.apache.spark.streaming.kafka010.{ConsumerStrategies, KafkaUtils, LocationStrategies}
import org.apache.spark.streaming.{Seconds, StreamingContext}

import java.util.Properties
import scala.collection.mutable

/**
 * Created with IntelliJ IDEA.
 * Author: CC
 * E-mail: 203717588@qq.com
 * Date: 2023/3/24
 * Time: 14:36
 * Description:
 */
object MeiTuanWaiMai_Category_Score {

  // Category维度
  // 业务：实时统计每个城市的每个种类(category)的评分(score)排名前10

  def main(args: Array[String]): Unit = {
    // 获取SparkSQL处理对象
    val spark = SparkSession.builder()
      .appName("Category_Score").master("local[*]").getOrCreate()
    val sc = spark.sparkContext
    // 获取Spark实时处理对象，微批处理时间间隔2s
    val ssc = new StreamingContext(sc,Seconds(2))

    // 消费Kafka数据配置
    val confMap = Map(
      ("key.deserializer","org.apache.kafka.common.serialization.StringDeserializer"),
      ("value.deserializer","org.apache.kafka.common.serialization.StringDeserializer"),
      ("bootstrap.servers","bd91:9092,bd92:9092,bd93:9092"),
      ("group.id","g1000"),
      ("auto.offset.reset","earliest")
//      ("enable.auto.commit","false")
    )
    // ODS层
    // 读取Kafka数据，获取DStream对象
    val ods_ds = KafkaUtils.createDirectStream[String,String](
      ssc,
      LocationStrategies.PreferConsistent,
      ConsumerStrategies.Subscribe[String,String](
        Iterable("ods_proSrc_log"),
        confMap
      )
    )

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
    val dws_ds = dwd_ds
      .map(t => ((t._1, t._4), CtgScInfo(t._1, t._4, t._2, t._3, t._5)))
      .repartition(1)
      .groupByKey()
      .map(_._2.toList)
      // List(CtgScInfo(福州市,甜点饮品,7615013,夏日午后,3.6), CtgScInfo(福州市,甜点饮品,7615013,夏日午后,1.2), CtgScInfo(福州市,甜点饮品,8445837,福心福全(奶茶.渔溪店),3.9), CtgScInfo(福州市,甜点饮品,7615013,夏日午后,4.8), CtgScInfo(福州市,甜点饮品,8445837,福心福全(奶茶.渔溪店),5.0), CtgScInfo(福州市,甜点饮品,7615013,夏日午后,0.4), CtgScInfo(福州市,甜点饮品,8445837,福心福全(奶茶.渔溪店),5.0), CtgScInfo(福州市,甜点饮品,7615013,夏日午后,1.2), CtgScInfo(福州市,甜点饮品,8445837,福心福全(奶茶.渔溪店),5.0), CtgScInfo(福州市,甜点饮品,7615013,夏日午后,1.2), CtgScInfo(福州市,甜点饮品,8445837,福心福全(奶茶.渔溪店),5.0), CtgScInfo(福州市,甜点饮品,7615013,夏日午后,2.4), CtgScInfo(福州市,甜点饮品,8445837,福心福全(奶茶.渔溪店),4.5), CtgScInfo(福州市,甜点饮品,7615013,夏日午后,3.6), CtgScInfo(福州市,甜点饮品,8445837,福心福全(奶茶.渔溪店),5.0), CtgScInfo(福州市,甜点饮品,7615013,夏日午后,2.8), CtgScInfo(福州市,甜点饮品,8445837,福心福全(奶茶.渔溪店),4.7), CtgScInfo(福州市,甜点饮品,7615013,夏日午后,2.0), CtgScInfo(福州市,甜点饮品,8445837,福心福全(奶茶.渔溪店),5.0), CtgScInfo(福州市,甜点饮品,7615013,夏日午后,1.2), CtgScInfo(福州市,甜点饮品,8445837,福心福全(奶茶.渔溪店),4.5), CtgScInfo(福州市,甜点饮品,7615013,夏日午后,4.8), CtgScInfo(福州市,甜点饮品,8445837,福心福全(奶茶.渔溪店),5.0), CtgScInfo(福州市,甜点饮品,7615013,夏日午后,1.2), CtgScInfo(福州市,甜点饮品,8445837,福心福全(奶茶.渔溪店),4.1), CtgScInfo(福州市,甜点饮品,7615013,夏日午后,1.2), CtgScInfo(福州市,甜点饮品,8445837,福心福全(奶茶.渔溪店),5.0), CtgScInfo(福州市,甜点饮品,7615013,夏日午后,3.6), CtgScInfo(福州市,甜点饮品,8445837,福心福全(奶茶.渔溪店),4.3), CtgScInfo(福州市,甜点饮品,7615013,夏日午后,3.6), CtgScInfo(福州市,甜点饮品,8445837,福心福全(奶茶.渔溪店),5.0), CtgScInfo(福州市,甜点饮品,7615013,夏日午后,1.2), CtgScInfo(福州市,甜点饮品,8445837,福心福全(奶茶.渔溪店),5.0), CtgScInfo(福州市,甜点饮品,7615013,夏日午后,2.8), CtgScInfo(福州市,甜点饮品,8445837,福心福全(奶茶.渔溪店),5.0), CtgScInfo(福州市,甜点饮品,7615013,夏日午后,0.4), CtgScInfo(福州市,甜点饮品,8445837,福心福全(奶茶.渔溪店),4.5), CtgScInfo(福州市,甜点饮品,7615013,夏日午后,0.0), CtgScInfo(福州市,甜点饮品,8445837,福心福全(奶茶.渔溪店),4.1), CtgScInfo(福州市,甜点饮品,7615013,夏日午后,0.0), CtgScInfo(福州市,甜点饮品,8445837,福心福全(奶茶.渔溪店),4.3), CtgScInfo(福州市,甜点饮品,7615013,夏日午后,1.2), CtgScInfo(福州市,甜点饮品,8445837,福心福全(奶茶.渔溪店),5.0), CtgScInfo(福州市,甜点饮品,7615013,夏日午后,4.8), CtgScInfo(福州市,甜点饮品,8445837,福心福全(奶茶.渔溪店),5.0), CtgScInfo(福州市,甜点饮品,7615013,夏日午后,1.2), CtgScInfo(福州市,甜点饮品,8445837,福心福全(奶茶.渔溪店),5.0), CtgScInfo(福州市,甜点饮品,7615013,夏日午后,4.8), CtgScInfo(福州市,甜点饮品,8445837,福心福全(奶茶.渔溪店),4.7), CtgScInfo(福州市,甜点饮品,7615013,夏日午后,1.2), CtgScInfo(福州市,甜点饮品,8445837,福心福全(奶茶.渔溪店),4.1), CtgScInfo(福州市,甜点饮品,7615013,夏日午后,1.2), CtgScInfo(福州市,甜点饮品,8445837,福心福全(奶茶.渔溪店),4.1), CtgScInfo(福州市,甜点饮品,7615013,夏日午后,2.4), CtgScInfo(福州市,甜点饮品,8445837,福心福全(奶茶.渔溪店),5.0), CtgScInfo(福州市,甜点饮品,7615013,夏日午后,0.4), CtgScInfo(福州市,甜点饮品,8445837,福心福全(奶茶.渔溪店),4.7), CtgScInfo(福州市,甜点饮品,7615013,夏日午后,1.2), CtgScInfo(福州市,甜点饮品,8445837,福心福全(奶茶.渔溪店),5.0), CtgScInfo(福州市,甜点饮品,7615013,夏日午后,1.2), CtgScInfo(福州市,甜点饮品,8445837,福心福全(奶茶.渔溪店),5.0), CtgScInfo(福州市,甜点饮品,7615013,夏日午后,0.0), CtgScInfo(福州市,甜点饮品,8445837,福心福全(奶茶.渔溪店),5.0), CtgScInfo(福州市,甜点饮品,7615013,夏日午后,1.2), CtgScInfo(福州市,甜点饮品,8445837,福心福全(奶茶.渔溪店),5.0), CtgScInfo(福州市,甜点饮品,9451521,花闲悦,2.4), CtgScInfo(福州市,甜点饮品,9451521,花闲悦,3.6), CtgScInfo(福州市,甜点饮品,9451521,花闲悦,1.2), CtgScInfo(福州市,甜点饮品,9451521,花闲悦,0.4), CtgScInfo(福州市,甜点饮品,9451521,花闲悦,2.0), CtgScInfo(福州市,甜点饮品,7615013,夏日午后,2.4), CtgScInfo(福州市,甜点饮品,8445837,福心福全(奶茶.渔溪店),5.0), CtgScInfo(福州市,甜点饮品,7615013,夏日午后,2.0), CtgScInfo(福州市,甜点饮品,8445837,福心福全(奶茶.渔溪店),5.0), CtgScInfo(福州市,甜点饮品,7615013,夏日午后,3.6), CtgScInfo(福州市,甜点饮品,8445837,福心福全(奶茶.渔溪店),5.0), CtgScInfo(福州市,甜点饮品,7615013,夏日午后,1.2), CtgScInfo(福州市,甜点饮品,8445837,福心福全(奶茶.渔溪店),5.0), CtgScInfo(福州市,甜点饮品,7615013,夏日午后,2.4), CtgScInfo(福州市,甜点饮品,8445837,福心福全(奶茶.渔溪店),4.5), CtgScInfo(福州市,甜点饮品,9451521,花闲悦,1.2), CtgScInfo(福州市,甜点饮品,9451521,花闲悦,0.4), CtgScInfo(福州市,甜点饮品,9451521,花闲悦,2.8), CtgScInfo(福州市,甜点饮品,9451521,花闲悦,0.4), CtgScInfo(福州市,甜点饮品,9451521,花闲悦,2.4), CtgScInfo(福州市,甜点饮品,9451521,花闲悦,3.6), CtgScInfo(福州市,甜点饮品,9451521,花闲悦,2.8), CtgScInfo(福州市,甜点饮品,9451521,花闲悦,0.4), CtgScInfo(福州市,甜点饮品,9451521,花闲悦,4.8), CtgScInfo(福州市,甜点饮品,9451521,花闲悦,1.2), CtgScInfo(福州市,甜点饮品,9451521,花闲悦,2.8), CtgScInfo(福州市,甜点饮品,9451521,花闲悦,3.6), CtgScInfo(福州市,甜点饮品,9451521,花闲悦,3.6), CtgScInfo(福州市,甜点饮品,9451521,花闲悦,3.6), CtgScInfo(福州市,甜点饮品,9451521,花闲悦,5.0), CtgScInfo(福州市,甜点饮品,9451521,花闲悦,0.4), CtgScInfo(福州市,甜点饮品,9451521,花闲悦,1.2), CtgScInfo(福州市,甜点饮品,9451521,花闲悦,5.0), CtgScInfo(福州市,甜点饮品,9451521,花闲悦,3.6), CtgScInfo(福州市,甜点饮品,9451521,花闲悦,2.4), CtgScInfo(福州市,甜点饮品,9451521,花闲悦,3.6), CtgScInfo(福州市,甜点饮品,9451521,花闲悦,3.6), CtgScInfo(福州市,甜点饮品,9451521,花闲悦,0.4), CtgScInfo(福州市,甜点饮品,9451521,花闲悦,2.0), CtgScInfo(福州市,甜点饮品,9451521,花闲悦,2.0), CtgScInfo(福州市,甜点饮品,9451521,花闲悦,3.6), CtgScInfo(福州市,甜点饮品,9451521,花闲悦,1.2), CtgScInfo(福州市,甜点饮品,9451521,花闲悦,3.6), CtgScInfo(福州市,甜点饮品,9451521,花闲悦,5.0), CtgScInfo(福州市,甜点饮品,9451521,花闲悦,3.6), CtgScInfo(福州市,甜点饮品,9451521,花闲悦,0.4), CtgScInfo(福州市,甜点饮品,9451521,花闲悦,5.0), CtgScInfo(福州市,甜点饮品,9451521,花闲悦,5.0))
      // id相同对象不同处理
      .map(ls=>{
        val map = new mutable.HashMap[String,CtgScInfo]()  // HashMap:key(id)相同覆盖
        ls.map(o=>(o.id,o))                                // (id,CtgScInfo)
          .foreach(t=>map.put(t._1,t._2))
        val list:List[CtgScInfo] = map.toList.map(_._2)    // Map[k,v] => List[(k,v)]
        list
      })

    // ADS层
    dws_ds.foreachRDD((rdd:RDD[List[CtgScInfo]])=>{
      val list:List[CtgScInfo] = rdd
        .repartition(1)   // 单分区操作
        .collect()
        .toList                        // List[List[CtgScInfo]]
        .flatMap(ls => {               // 最终结果扁平化
          ls.sortBy(_.score)           // 组内List[CtgScInfo]排序
          .reverse                     // 降序
          .take(10)                    // Top10
      })
      // 连接属性
      val prop = new Properties()
      prop.setProperty("user","root")
      prop.setProperty("password","123456")
      // 导入转换包
      import spark.implicits._
      val df = list.toDF("city","category","id","name","score")
      df.write.mode(SaveMode.Overwrite).jdbc(
        "jdbc:mysql://localhost:3306/day0322_bd_pro?serverTimezone=UTC",
        "area_category_score_top10",
        prop
      )
    })

    // 开启任务
    ssc.start()
    ssc.awaitTermination()

  }

}

case class CtgScInfo(var city:String,
                     var category:String,
                     var id:String,
                     var name:String,
                     var score:Double
                    )
