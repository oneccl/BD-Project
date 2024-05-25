package com.cc.utils

/**
 * Created with IntelliJ IDEA.
 * Author: CC
 * E-mail: 203717588@qq.com
 * Date: 2023/3/23
 * Time: 9:41
 * Description:
 */
object ScoreStrategy {

  def main(args: Array[String]): Unit = {

    for (i:Int <- 0 to 10){
      println(generator(4.6))
    }

  }

  def generator(score:Double):Double={
    val ran= Math.random()  // [0.0,1.0)
    var targetScore = score
    var genScore = 0.0
    if (targetScore==0.0){
      targetScore = Double.box(ran).formatted("%.1f").toDouble * 5
    }
    val d = Double.box(ran).formatted("%.1f").toDouble * 10
    if (d%2==0){
      genScore = targetScore + ran
    } else {
      genScore = targetScore - ran
    }
    val newScore = Double.box(genScore).formatted("%.1f").toDouble
    if (newScore>5) 5.0 else if (newScore<0) 0.0 else newScore
  }

}
