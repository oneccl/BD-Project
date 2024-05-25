package com.cc.utils

/**
 * Created with IntelliJ IDEA.
 * Author: CC
 * E-mail: 203717588@qq.com
 * Date: 2023/3/23
 * Time: 9:14
 * Description:
 */
object StringUtil {

  def main(args: Array[String]): Unit = {

    val s = "月售100+时间2021年3月20日"
    println(getNumber(s, 1))

  }

  // 获取字符串中指定位置的数
  def getNumber(str:String, index:Int):String={
    val arr = str.split("[^0-9]+").filter(_.nonEmpty)
    arr(index-1)
  }

}
