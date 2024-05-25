package com.cc

import com.cc.ads.ADS
import com.cc.dwd.DWD
import com.cc.dws.DWS
import com.cc.ods.ODS
import org.apache.spark.sql.SparkSession
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
 * Created with IntelliJ IDEA.
 * Author: CC
 * E-mail: 203717588@qq.com
 * Date: 2023/3/29
 * Time: 14:37
 * Description:
 */
object SparkApplication {

  def main(args: Array[String]): Unit = {
    // 获取SparkSQL处理对象
    val spark = SparkSession.builder()
      .appName("SparkApplication").master("local[*]").getOrCreate()
    // 获取SparkCore核心对象
    val sc = spark.sparkContext
    // 获取Spark实时处理对象，微批处理时间间隔2s
    val ssc = new StreamingContext(sc,Seconds(2))

    // ODS层
    val ods_ds = ODS.ods(ssc)

    // DWD层
    val dwd_ds = DWD.dwd(ods_ds)

    // DWS层
    // 城市维度
    val dws_dimen1 = DWS.dwsDimen1(dwd_ds)
    // 类别维度
    val dws_dimen2 = DWS.dwsDimen2(dwd_ds)
    // 类别维度
    val dws_dimen3 = DWS.dwsDimen3(dwd_ds)

    // ADS层
    ADS.adsDimen1(dws_dimen1, sc, spark)
    ADS.adsDimen2(dws_dimen2, sc, spark)
    ADS.adsDimen3(dws_dimen3, sc, spark)

    // 开启任务
    ssc.start()
    ssc.awaitTermination()

  }

}
