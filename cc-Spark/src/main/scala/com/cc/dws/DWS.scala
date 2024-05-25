package com.cc.dws

import com.cc.{CtgMSInfo, CtgScInfo, LogInfo}
import org.apache.spark.streaming.dstream.DStream

import scala.collection.mutable

/**
 * Created with IntelliJ IDEA.
 * Author: CC
 * E-mail: 203717588@qq.com
 * Date: 2023/3/29
 * Time: 14:48
 * Description:
 */
object DWS {

  def dwsDimen1(dwd:DStream[(String, String, String, String, Double, Int, Long)]): DStream[List[LogInfo]] ={
    val dws_ds:DStream[List[LogInfo]] = dwd
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
    dws_ds
  }

  def dwsDimen2(dwd:DStream[(String, String, String, String, Double, Int, Long)]):DStream[List[CtgScInfo]] ={
    // DWS层
    val dws_ds = dwd
      .map(t => ((t._1, t._4), CtgScInfo(t._1, t._4, t._2, t._3, t._5)))
      .repartition(1)
      .groupByKey()
      .map(_._2.toList)
      // id相同对象不同处理
      .map(ls=>{
        val map = new mutable.HashMap[String,CtgScInfo]()  // HashMap:key(id)相同覆盖
        ls.map(o=>(o.id,o))                                // (id,CtgScInfo)
          .foreach(t=>map.put(t._1,t._2))
        val list:List[CtgScInfo] = map.toList.map(_._2)    // Map[k,v] => List[(k,v)]
        list
      })
    dws_ds
  }

  def dwsDimen3(dwd:DStream[(String, String, String, String, Double, Int, Long)]): DStream[((String, String, String), CtgMSInfo)] ={
    val dws_ds = dwd
      .map(t => ((t._1,t._4,t._2), CtgMSInfo(t._1, t._4, t._2, t._3, t._7)))
      .reduceByKey((o1, o2) => o1.sum(o2))                      // 单批次聚合
      .updateStateByKey((values:Seq[CtgMSInfo],stateRes:Option[CtgMSInfo])=>{
        if (values.nonEmpty){
          val valuesSum = values.reduce((o1, o2) => o1.sum(o2)) // 当前批次聚合
          stateRes match {
            case None => Option(valuesSum)
            case _ => Option(valuesSum.sum(stateRes.get))       // 累加历史数据
          }
        } else stateRes
      })
    dws_ds
  }

}
