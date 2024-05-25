package com.cc.dwd

import com.cc.utils.{ScoreStrategy, StringUtil}
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.spark.streaming.dstream.DStream

/**
 * Created with IntelliJ IDEA.
 * Author: CC
 * E-mail: 203717588@qq.com
 * Date: 2023/3/29
 * Time: 14:42
 * Description:
 */
object DWD {

  def dwd(ods: DStream[ConsumerRecord[String,String]]): DStream[(String, String, String, String, Double, Int, Long)] ={
    val dwd_ds = ods
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
    dwd_ds
  }

}
