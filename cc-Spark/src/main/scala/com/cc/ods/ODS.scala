package com.cc.ods

import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.spark.sql.SparkSession
import org.apache.spark.streaming.dstream.DStream
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.streaming.kafka010.{ConsumerStrategies, KafkaUtils, LocationStrategies}

/**
 * Created with IntelliJ IDEA.
 * Author: CC
 * E-mail: 203717588@qq.com
 * Date: 2023/3/23
 * Time: 11:41
 * Description:
 */
object ODS {

  def ods(ssc:StreamingContext): DStream[ConsumerRecord[String,String]] ={
    // 消费Kafka数据配置
    val confMap = Map(
      ("key.deserializer","org.apache.kafka.common.serialization.StringDeserializer"),
      ("value.deserializer","org.apache.kafka.common.serialization.StringDeserializer"),
      ("bootstrap.servers","bd91:9092,bd92:9092,bd93:9092"),
      ("group.id","g1000"),
      ("auto.offset.reset","earliest")
    )
    // 读取Kafka数据，获取DStream对象
    val ods_ds = KafkaUtils.createDirectStream[String,String](
      ssc,
      LocationStrategies.PreferConsistent,
      ConsumerStrategies.Subscribe[String,String](
        Iterable("ods_proSrc_log"),
        confMap
      )
    )
    ods_ds
  }

}
