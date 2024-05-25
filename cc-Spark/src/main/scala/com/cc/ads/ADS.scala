package com.cc.ads

import com.cc.{CtgMSInfo, CtgScInfo, LogInfo}
import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{SaveMode, SparkSession}
import org.apache.spark.streaming.dstream.DStream

import java.util.Properties

/**
 * Created with IntelliJ IDEA.
 * Author: CC
 * E-mail: 203717588@qq.com
 * Date: 2023/3/29
 * Time: 15:03
 * Description:
 */
object ADS {

  def adsDimen1(dws:DStream[List[LogInfo]],sc:SparkContext,spark:SparkSession): Unit ={
    // 连接属性
    val prop = new Properties()
    prop.setProperty("user","root")
    prop.setProperty("password","123456")
    // 导入转换包
    import spark.implicits._
    dws.foreachRDD(rdd=>{
      val list = rdd.collect().toList.flatten
      val df = sc.makeRDD(list).toDF("city", "id", "name", "category", "score")
      df.write.mode(SaveMode.Overwrite).jdbc(
        "jdbc:mysql://localhost:3306/day0322_bd_pro?serverTimezone=UTC",
        "area_score_top10",
        prop
      )
    })
  }

  def adsDimen2(dws:DStream[List[CtgScInfo]],sc:SparkContext,spark:SparkSession): Unit ={
    dws.foreachRDD((rdd:RDD[List[CtgScInfo]])=>{
      val list:List[CtgScInfo] = rdd
        .repartition(1)   // 单分区操作
        .collect()
        .toList                        // List[List[CtgScInfo]]
        .flatMap(ls => {               // 最终结果扁平化
          ls.sortBy(_.score)           // 组内List[CtgScInfo]排序
            .reverse                   // 降序
            .take(10)                  // Top10
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
  }

  def adsDimen3(dws:DStream[((String, String, String), CtgMSInfo)],sc:SparkContext,spark:SparkSession): Unit ={
    dws.foreachRDD((rdd:RDD[((String,String,String),CtgMSInfo)])=>{
      val list:List[List[CtgMSInfo]] = rdd
        .repartition(1)         // 单分区内操作
        .map(_._2)                           // 获取元组第二项CtgMSInfo
        .groupBy(o=>(o.city,o.category))     // 根据城市和种类分组
        .map(_._2.toList)                    // RDD[List[list[CtgMSInfo]]]
        .map(ls => {                         // 处理List中每组数据
          ls.sortBy(_.month_sales)           // 根据月销量排序（升序）
            .reverse                         // 降序
            .take(10)                        // Top10
        })
        .collect()                           // 收集
        .toList
      val ls:List[CtgMSInfo] = list.flatten  // 扁平化
      // 连接属性
      val prop = new Properties()
      prop.setProperty("user","root")
      prop.setProperty("password","123456")
      // 导入转换包
      import spark.implicits._
      val df = sc.makeRDD(ls).toDF("city", "category", "id", "name", "month_sales")
      df.write.mode(SaveMode.Overwrite).jdbc(
        "jdbc:mysql://localhost:3306/day0322_bd_pro?serverTimezone=UTC",
        "area_category_month_sales_top10",
        prop
      )
    })
  }

}
